import js from '@eslint/js';

export default [
  js.configs.recommended,
  {
    files: ['assets/**/*.js', 'tests/**/*.js'],
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: 'module',
      globals: {
        window: 'readonly',
        document: 'readonly',
        navigator: 'readonly',
        fetch: 'readonly',
        alert: 'readonly',
        FormData: 'readonly',
        URL: 'readonly',
        IntersectionObserver: 'readonly',
        requestAnimationFrame: 'readonly',
        cancelAnimationFrame: 'readonly',
        setInterval: 'readonly',
        clearInterval: 'readonly',
        setTimeout: 'readonly',
        clearTimeout: 'readonly',
      },
    },
    rules: {
      'no-unused-vars': ['warn', { argsIgnorePattern: '^_', caughtErrors: 'none' }],
      // catch vazio é usado intencionalmente como degradação silenciosa
      // (o site nunca quebra por falha de um bloco de conteúdo). Ver
      // doc/02-ARQUITETURA-CANONICA.md (política de falha).
      'no-empty': ['error', { allowEmptyCatch: true }],
    },
  },
];
