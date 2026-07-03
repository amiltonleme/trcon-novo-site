import { describe, it, expect } from 'vitest';
import { resolveApiConfig } from '../../assets/modules/config.js';

describe('resolveApiConfig', () => {
  it('usa defaults locais quando nenhuma variável é injetada', () => {
    const cfg = resolveApiConfig({
      TRCON_LEADS_API_URL: undefined,
      TRCON_WAITLIST_API_URL: undefined,
      TRCON_HIGHLIGHTS_API_URL: undefined,
      TRCON_NEWS_API_URL: undefined,
    });
    expect(cfg.leadsApiUrl).toBe('http://localhost:8080/api/v1/site/leads');
    expect(cfg.highlightsApiUrl).toBe('');
    expect(cfg.newsApiUrl).toBe('');
  });

  it('prioriza TRCON_LEADS_API_URL sobre o legado waitlist', () => {
    const cfg = resolveApiConfig({
      TRCON_LEADS_API_URL: 'https://api.trcon.com.br/api/v1/site/leads',
      TRCON_WAITLIST_API_URL: 'https://legado/waitlist',
    });
    expect(cfg.leadsApiUrl).toBe('https://api.trcon.com.br/api/v1/site/leads');
  });

  it('cai no endpoint legado de waitlist quando só ele existe', () => {
    const cfg = resolveApiConfig({
      TRCON_LEADS_API_URL: undefined,
      TRCON_WAITLIST_API_URL: 'https://legado/waitlist',
    });
    expect(cfg.leadsApiUrl).toBe('https://legado/waitlist');
  });

  it('propaga URLs de highlights e news quando injetadas', () => {
    const cfg = resolveApiConfig({
      TRCON_HIGHLIGHTS_API_URL: 'https://api.trcon.com.br/api/public/highlights',
      TRCON_NEWS_API_URL: 'https://api.trcon.com.br/api/public/news',
    });
    expect(cfg.highlightsApiUrl).toBe('https://api.trcon.com.br/api/public/highlights');
    expect(cfg.newsApiUrl).toBe('https://api.trcon.com.br/api/public/news');
  });
});
