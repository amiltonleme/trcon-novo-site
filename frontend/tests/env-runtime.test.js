import { readFileSync } from 'node:fs';
import { runInNewContext } from 'node:vm';
import { describe, expect, it } from 'vitest';

const source = readFileSync(new URL('../assets/env.js', import.meta.url), 'utf8');

function loadRuntimeEnv(hostname, origin) {
  const window = { location: { hostname, origin } };
  runInNewContext(source, { window });
  return window;
}

describe('env.js em runtime', () => {
  it('usa o proxy da própria origem em preview do Coolify', () => {
    const env = loadRuntimeEnv('site-preview.example.coolify.io', 'https://site-preview.example.coolify.io');

    expect(env.TRCON_HIGHLIGHTS_API_URL)
      .toBe('https://site-preview.example.coolify.io/api/public/highlights');
    expect(env.TRCON_CHAT_API_URL)
      .toBe('https://site-preview.example.coolify.io/api/v1/site/chat');
  });

  it('mantém localhost para desenvolvimento executado na máquina', () => {
    const env = loadRuntimeEnv('localhost', 'http://localhost:4173');

    expect(env.TRCON_HIGHLIGHTS_API_URL).toBe('http://localhost:8081/api/public/highlights');
  });

  it('mantém os domínios públicos explícitos do ambiente dev', () => {
    const env = loadRuntimeEnv('site-dev.trcongroup.com.br', 'https://site-dev.trcongroup.com.br');

    expect(env.TRCON_HIGHLIGHTS_API_URL)
      .toBe('https://api-site-dev.trcongroup.com.br/api/public/highlights');
  });
});
