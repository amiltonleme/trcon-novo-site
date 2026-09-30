import { execSync } from 'node:child_process';
import { createHash } from 'node:crypto';
import { readFileSync, readdirSync, statSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = join(dirname(fileURLToPath(import.meta.url)), '..');
const pkg = JSON.parse(readFileSync(join(root, 'package.json'), 'utf8'));
const version = pkg.version;

function assetFingerprint() {
  const files = [];
  const visit = directory => {
    for (const name of readdirSync(directory).sort()) {
      const path = join(directory, name);
      if (statSync(path).isDirectory()) visit(path);
      else if (/\.(?:css|js)$/.test(name)) files.push(path);
    }
  };
  visit(join(root, 'assets'));
  for (const name of ['style.css', 'article.css', 'legal.css']) {
    const path = join(root, name);
    try {
      if (statSync(path).isFile()) files.push(path);
    } catch {
      // Folha opcional ausente nesta versão do site.
    }
  }
  const hash = createHash('sha256');
  for (const path of files.sort()) hash.update(readFileSync(path));
  return hash.digest('hex').slice(0, 12);
}

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
    // A imagem do Coolify não contém o diretório .git. O hash do conteúdo
    // garante URLs novas mesmo quando SOURCE_COMMIT não foi passado ao build.
    return assetFingerprint();
  }
}

const commit = resolveCommit(process.argv[2]);
const htmlPath = join(root, 'index.html');
let html = readFileSync(htmlPath, 'utf8');

function stampCss(content) {
  return content.replace(
    /(href="\/?(?:style|article|legal)\.css)(?:\?v=[^"]+)?(")/g,
    `$1?v=${commit}$2`
  );
}

function stampModuleImports(content) {
  return content.replace(
    /((?:\bfrom\s*|\bimport\s*(?:\(\s*)?)['"])(\.{1,2}\/[^'"?]+)(?:\?v=[^'"]+)?(['"])/g,
    `$1$2?v=${commit}$3`
  );
}

function visitJavaScript(directory) {
  for (const name of readdirSync(directory).sort()) {
    const path = join(directory, name);
    if (statSync(path).isDirectory()) {
      visitJavaScript(path);
    } else if (name.endsWith('.js')) {
      const source = readFileSync(path, 'utf8');
      const stamped = stampModuleImports(source);
      if (stamped !== source) writeFileSync(path, stamped, 'utf8');
    }
  }
}

html = html.replace(/(<span id="siteAppVersion">)[^<]*(<\/span>)/, `$1${version}$2`);
html = html.replace(/(<code id="siteCommitHash">)[^<]*(<\/code>)/, `$1${commit}$2`);

// Cache-busting: reaproveita o mesmo hash de commit já calculado acima para
// versionar a URL do JS/CSS. Sem isso, o rodapé mudava de versão a cada deploy
// mas o navegador de quem já visitou o site continuava servindo o app.js/style.css
// antigos do próprio cache (assets/app.js é sempre a mesma URL, então o
// Cache-Control de 4h do nginx nunca era invalidado por um deploy novo).
// Regex idempotente: troca um ?v= antigo se já existir, ou adiciona um novo.
html = html.replace(
  /(src="assets\/app\.js)(?:\?v=[^"]+)?(")/,
  `$1?v=${commit}$2`
);
html = stampCss(html);

// Cada módulo ES recebe a mesma versão do documento. Sem isso, o app.js novo
// pode importar um módulo antigo mantido no cache da CDN e toda a aplicação
// deixa de inicializar por incompatibilidade entre exports.
visitJavaScript(join(root, 'assets'));

writeFileSync(htmlPath, html, 'utf8');

for (const staticPage of ['novidades.html', 'privacidade.html']) {
  const pagePath = join(root, staticPage);
  const pageHtml = stampCss(readFileSync(pagePath, 'utf8'));
  writeFileSync(pagePath, pageHtml, 'utf8');
}

console.log(`Stamped site footer: v${version} @ ${commit} (cache-busting aplicado em módulos JS/CSS)`);
