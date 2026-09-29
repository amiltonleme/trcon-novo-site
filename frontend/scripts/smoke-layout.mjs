import { spawn } from 'node:child_process';
import { existsSync, mkdtempSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';

const browserCandidates = [
  process.env.BROWSER_PATH,
  'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe',
  'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
  '/usr/bin/microsoft-edge',
  '/usr/bin/google-chrome',
  '/usr/bin/chromium',
].filter(Boolean);
const browserPath = browserCandidates.find(existsSync);
if (!browserPath) throw new Error('Navegador Chromium não encontrado. Defina BROWSER_PATH.');

const baseUrl = process.env.SMOKE_BASE_URL || 'http://127.0.0.1:4173';
const routes = [
  'home', 'sobre', 'como-ajudamos', 'servicos', 'produtos', 'conteudo',
  'carreiras', 'contato', 'hub', 'agendamento', 'marketing',
];
const viewports = [
  { name: 'desktop', width: 1440, height: 900 },
  { name: 'mobile', width: 390, height: 844 },
];
const screenshotRoutes = new Set(['home', 'sobre', 'servicos', 'produtos', 'carreiras', 'contato']);
const profileDir = mkdtempSync(join(tmpdir(), 'trcon-layout-smoke-'));
const debugPort = 9323;
let devServer;
const browser = spawn(browserPath, [
  '--headless=new',
  '--disable-gpu',
  '--hide-scrollbars',
  `--remote-debugging-port=${debugPort}`,
  `--user-data-dir=${profileDir}`,
  'about:blank',
], { stdio: 'ignore' });

const wait = milliseconds => new Promise(resolve => setTimeout(resolve, milliseconds));

async function fetchJson(url, options) {
  const response = await fetch(url, options);
  if (!response.ok) throw new Error(`${response.status} ao acessar ${url}`);
  return response.json();
}

async function waitForDebugger() {
  for (let attempt = 0; attempt < 40; attempt += 1) {
    try {
      return await fetchJson(`http://127.0.0.1:${debugPort}/json/version`);
    } catch {
      await wait(100);
    }
  }
  throw new Error('Edge não disponibilizou o protocolo de depuração.');
}

async function ensureSite() {
  try {
    const response = await fetch(`${baseUrl}/`);
    if (response.ok && (await response.text()).includes('id="page-home"')) return;
  } catch {
    // O servidor local será iniciado abaixo.
  }
  const target = new URL(baseUrl);
  if (target.hostname !== '127.0.0.1' && target.hostname !== 'localhost') {
    throw new Error(`SMOKE_BASE_URL indisponível: ${baseUrl}`);
  }
  devServer = spawn(process.execPath, [join(import.meta.dirname, 'dev_server.js')], {
    env: { ...process.env, PORT: target.port || '4173' },
    stdio: 'ignore',
  });
  for (let attempt = 0; attempt < 40; attempt += 1) {
    try {
      const response = await fetch(`${baseUrl}/`);
      if (response.ok && (await response.text()).includes('id="page-home"')) return;
    } catch {
      await wait(100);
    }
  }
  throw new Error(`Servidor local não iniciou em ${baseUrl}`);
}

function connect(webSocketDebuggerUrl) {
  const socket = new WebSocket(webSocketDebuggerUrl);
  const pending = new Map();
  const issues = [];
  let nextId = 1;
  socket.addEventListener('message', event => {
    const message = JSON.parse(event.data);
    if (message.method === 'Runtime.exceptionThrown') {
      issues.push({
        source: 'runtime',
        text: message.params.exceptionDetails.text,
        url: message.params.exceptionDetails.url || '',
      });
    }
    if (message.method === 'Log.entryAdded' && message.params.entry.level === 'error') {
      issues.push({
        source: 'log',
        text: message.params.entry.text,
        url: message.params.entry.url || '',
      });
    }
    if (!message.id || !pending.has(message.id)) return;
    const { resolve, reject } = pending.get(message.id);
    pending.delete(message.id);
    if (message.error) reject(new Error(message.error.message));
    else resolve(message.result);
  });
  const opened = new Promise((resolve, reject) => {
    socket.addEventListener('open', resolve, { once: true });
    socket.addEventListener('error', reject, { once: true });
  });
  return {
    opened,
    issues,
    close: () => socket.close(),
    request(method, params = {}) {
      const id = nextId;
      nextId += 1;
      return new Promise((resolve, reject) => {
        pending.set(id, { resolve, reject });
        socket.send(JSON.stringify({ id, method, params }));
      });
    },
  };
}

try {
  await ensureSite();
  await waitForDebugger();
  const page = await fetchJson(`http://127.0.0.1:${debugPort}/json/new?about:blank`, { method: 'PUT' });
  const client = connect(page.webSocketDebuggerUrl);
  await client.opened;
  await client.request('Page.enable');
  await client.request('Runtime.enable');
  await client.request('Log.enable');

  const results = [];
  let navigationId = 0;
  for (const viewport of viewports) {
    await client.request('Emulation.setDeviceMetricsOverride', {
      width: viewport.width,
      height: viewport.height,
      deviceScaleFactor: 1,
      mobile: viewport.name === 'mobile',
    });
    for (const route of routes) {
      navigationId += 1;
      await client.request('Page.navigate', { url: `${baseUrl}/?smoke=${navigationId}#${route}` });
      await wait(700);
      const evaluation = await client.request('Runtime.evaluate', {
        expression: `(() => {
          const root = document.documentElement;
          const body = document.body;
          const active = document.querySelector('.page.active');
          const careerPage = location.hash.slice(1) === 'carreiras' ? active : null;
          const headings = careerPage ? [...careerPage.querySelectorAll('h1, h2')].map(heading => ({
            level: Number(heading.tagName.slice(1)),
            text: heading.textContent.trim(),
          })) : [];
          const headingOrderValid = headings.every((heading, index) =>
            index === 0 || heading.level <= headings[index - 1].level + 1
          );
          const careerChecks = careerPage ? {
            hasSingleH1: careerPage.querySelectorAll('h1').length === 1,
            headingOrderValid,
            labelledSections: [...careerPage.querySelectorAll('section[aria-labelledby]')].every(section => {
              const labelId = section.getAttribute('aria-labelledby');
              return labelId && section.querySelector('#' + CSS.escape(labelId));
            }),
            faqCount: careerPage.querySelectorAll('.career-faq details > summary').length,
            emptyVacanciesClear: careerPage.textContent.includes('não há vagas abertas publicadas'),
            talentBankClear: careerPage.textContent.includes('não há banco de talentos disponível'),
            hasCandidateCollection: Boolean(careerPage.querySelector('form, input, textarea, [type="file"]')),
            unnamedLinks: [...careerPage.querySelectorAll('a')].filter(link => !link.textContent.trim() && !link.getAttribute('aria-label')).length,
          } : null;
          return {
            route: location.hash.slice(1),
            activePage: active?.id || null,
            viewportWidth: innerWidth,
            documentWidth: root.scrollWidth,
            bodyWidth: body.scrollWidth,
            overflow: root.scrollWidth > innerWidth || body.scrollWidth > innerWidth,
            careerChecks,
          };
        })()`,
        returnByValue: true,
      });
      results.push({
        viewport: viewport.name,
        requestedRoute: route,
        expectedPage: `page-${route}`,
        ...evaluation.result.value,
      });
      if (screenshotRoutes.has(route)) {
        await wait(route === 'home' ? 1800 : 300);
        const screenshot = await client.request('Page.captureScreenshot', { format: 'png' });
        writeFileSync(join(profileDir, `${route}-${viewport.name}.png`), screenshot.data, 'base64');
      }
    }
  }

  client.close();
  const failures = results.filter(result =>
    result.overflow || result.activePage !== result.expectedPage ||
    (result.careerChecks && (
      !result.careerChecks.hasSingleH1 ||
      !result.careerChecks.headingOrderValid ||
      !result.careerChecks.labelledSections ||
      result.careerChecks.faqCount !== 5 ||
      !result.careerChecks.emptyVacanciesClear ||
      !result.careerChecks.talentBankClear ||
      result.careerChecks.hasCandidateCollection ||
      result.careerChecks.unnamedLinks > 0
    ))
  );
  const criticalBrowserIssues = client.issues.filter(issue =>
    issue.source === 'runtime' || issue.url.startsWith(baseUrl)
  );
  console.log(JSON.stringify({
    baseUrl,
    profileDir,
    checkedRoutes: routes.length,
    checkedViewports: viewports.map(viewport => `${viewport.width}x${viewport.height}`),
    checks: results.length,
    failures,
    criticalBrowserIssues,
    ignoredUnavailableApiRequests: client.issues.length - criticalBrowserIssues.length,
  }, null, 2));
  if (failures.length || criticalBrowserIssues.length) process.exitCode = 1;
} finally {
  browser.kill();
  devServer?.kill();
  await wait(250);
  // Preserve screenshots for visual inspection; the caller removes this temporary directory.
}
