import { execSync } from 'node:child_process';
import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = join(dirname(fileURLToPath(import.meta.url)), '..');
const pkg = JSON.parse(readFileSync(join(root, 'package.json'), 'utf8'));

function normalizeCommit(raw) {
  if (!raw) return null;
  const value = String(raw).trim();
  return value ? value.slice(0, 12) : null;
}

function resolveCommitHash() {
  const fromEnv = normalizeCommit(
    process.env.GIT_COMMIT ??
      process.env.SOURCE_COMMIT ??
      process.env.COOLIFY_SOURCE_COMMIT ??
      process.env.GITHUB_SHA,
  );
  if (fromEnv) return fromEnv;

  try {
    return execSync('git rev-parse --short HEAD', {
      cwd: join(root, '..'),
      encoding: 'utf8',
      stdio: ['ignore', 'pipe', 'ignore'],
    }).trim();
  } catch {
    return 'unknown';
  }
}

const appVersion = pkg.version;
const commitHash = resolveCommitHash();

const content = `// Gerado por scripts/inject-build-info.mjs — não editar manualmente.
window.TRCON_APP_VERSION = '${appVersion}';
window.TRCON_COMMIT_HASH = '${commitHash}';
`;

writeFileSync(join(root, 'assets/build-info.js'), content, 'utf8');
console.log(`Build info: v${appVersion} @ ${commitHash}`);
