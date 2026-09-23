import { execSync } from 'node:child_process';
import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = join(dirname(fileURLToPath(import.meta.url)), '..');
const pkg = JSON.parse(readFileSync(join(root, 'package.json'), 'utf8'));
const version = pkg.version;

function resolveCommit(cliArg) {
  const raw = cliArg || process.env.GITHUB_SHA || process.env.SOURCE_COMMIT || '';
  if (raw && raw !== 'unknown') {
    return String(raw).trim().slice(0, 7);
  }
  try {
    return execSync('git rev-parse --short HEAD', {
      cwd: join(root, '..'),
      encoding: 'utf8',
      stdio: ['ignore', 'pipe', 'ignore'],
    }).trim();
  } catch {
    return 'dev';
  }
}

const commit = resolveCommit(process.argv[2]);
const htmlPath = join(root, 'index.html');
let html = readFileSync(htmlPath, 'utf8');

html = html.replace(/(<span id="siteAppVersion">)[^<]*(<\/span>)/, `$1${version}$2`);
html = html.replace(/(<code id="siteCommitHash">)[^<]*(<\/code>)/, `$1${commit}$2`);

// Cache-busting: reaproveita o mesmo hash de commit já calculado acima para
// versionar a URL do JS/CSS. Sem isso, o rodapé mudava de versão a cada deploy
// mas o navegador de quem já visitou o site continuava servindo o app.js/style.css
// antigos do próprio cache (assets/app.js é sempre a mesma URL, então o
// Cache-Control de 4h do nginx nunca era invalidado por um deploy novo).
// Regex idempotente: troca um ?v= antigo se já existir, ou adiciona um novo.
html = html.replace(
  /(src="assets\/app\.js)(?:\?v=[a-f0-9]+)?(")/,
  `$1?v=${commit}$2`
);
html = html.replace(
  /(href="style\.css)(?:\?v=[a-f0-9]+)?(")/,
  `$1?v=${commit}$2`
);

writeFileSync(htmlPath, html, 'utf8');
console.log(`Stamped site footer: v${version} @ ${commit} (cache-busting aplicado em app.js e style.css)`);
