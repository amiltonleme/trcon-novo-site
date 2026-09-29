import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, join, relative } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = join(dirname(fileURLToPath(import.meta.url)), '..');
const checkOnly = process.argv.includes('--check');

const shared = [
  'styles/tokens.css',
  'styles/base.css',
  'styles/layouts/navigation.css',
];

const manifests = {
  'style.css': [
    ...shared,
    'styles/layouts/page-shell.css',
    'styles/pages/home-legacy-hero.css',
    'styles/pages/home-hero.css',
    'styles/pages/home-hero-responsive.css',
    'styles/layouts/sections.css',
    'styles/components/cards.css',
    'styles/components/hub-gallery.css',
    'styles/pages/company.css',
    'styles/pages/offers.css',
    'styles/components/subpage-hero.css',
    'styles/components/product-cards.css',
    'styles/components/forms.css',
    'styles/pages/audiences.css',
    'styles/layouts/footer.css',
    'styles/components/mobile-nav.css',
    'styles/components/floating-actions.css',
  ],
  'article.css': [...shared, 'styles/pages/article.css'],
  'legal.css': [...shared, 'styles/pages/article.css', 'styles/pages/legal.css'],
};

function buildBundle(sources) {
  const header = [
    '/* GENERATED FILE. Edit frontend/styles/** and run npm run build:css. */',
    `/* Sources: ${sources.join(', ')} */`,
    '',
  ].join('\n');
  const body = sources
    .map((source) => readFileSync(join(root, source), 'utf8').trimEnd())
    .join('\n\n');
  return `${header}${body}\n`;
}

let stale = false;
for (const [outputName, sources] of Object.entries(manifests)) {
  const outputPath = join(root, outputName);
  const expected = buildBundle(sources);
  if (checkOnly) {
    let actual = '';
    try {
      actual = readFileSync(outputPath, 'utf8').replace(/\r\n/g, '\n');
    } catch {
      // Arquivo ausente é tratado como bundle desatualizado.
    }
    if (actual !== expected) {
      stale = true;
      console.error(`Bundle desatualizado: ${relative(root, outputPath)}`);
    }
  } else {
    writeFileSync(outputPath, expected, 'utf8');
    console.log(`Bundle gerado: ${relative(root, outputPath)}`);
  }
}

if (stale) process.exitCode = 1;
