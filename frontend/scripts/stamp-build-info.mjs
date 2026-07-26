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

writeFileSync(htmlPath, html, 'utf8');
console.log(`Stamped site footer: v${version} @ ${commit}`);
