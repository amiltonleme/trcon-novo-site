import { describe, it, expect, vi } from 'vitest';
import {
  extractItems,
  fetchWithFallback,
  buildHighlightsHtml,
  buildNewsHtml,
  loadEconomyTips,
  filterRadarDuplicates,
  isEditorialHighlight,
  fetchRadarHighlights,
} from '../../assets/modules/content.js';

const ok = (data) => ({ ok: true, json: () => Promise.resolve(data) });
const fail = (status = 500) => ({ ok: false, status, json: () => Promise.resolve({}) });

describe('extractItems', () => {
  it('lê items do envelope', () => {
    expect(extractItems({ items: [1, 2] })).toEqual([1, 2]);
  });
  it('aceita array puro', () => {
    expect(extractItems([1])).toEqual([1]);
  });
  it('retorna [] para payload inválido', () => {
    expect(extractItems(null)).toEqual([]);
    expect(extractItems({})).toEqual([]);
  });
});

describe('fetchWithFallback', () => {
  it('usa a API quando disponível', async () => {
    const fetchImpl = vi.fn().mockResolvedValue(ok({ items: [{ title: 'a' }] }));
    const res = await fetchWithFallback('http://api/highlights', 'data/x.json', { fetch: fetchImpl });
    expect(res.source).toBe('api');
    expect(res.items).toHaveLength(1);
    expect(fetchImpl).toHaveBeenCalledTimes(1);
  });

  it('cai para o JSON quando a API responde lista vazia', async () => {
    const fetchImpl = vi
      .fn()
      .mockResolvedValueOnce(ok({ items: [] }))
      .mockResolvedValueOnce(ok({ items: [{ title: 'json-radar' }] }));
    const res = await fetchWithFallback('http://api/highlights', 'data/x.json', { fetch: fetchImpl });
    expect(res.source).toBe('json');
    expect(res.items[0].title).toBe('json-radar');
    expect(fetchImpl).toHaveBeenCalledTimes(2);
  });

  it('cai para o JSON quando a API responde erro', async () => {
    const fetchImpl = vi
      .fn()
      .mockResolvedValueOnce(fail(503))
      .mockResolvedValueOnce(ok({ items: [{ title: 'json' }] }));
    const res = await fetchWithFallback('http://api/highlights', 'data/x.json', { fetch: fetchImpl });
    expect(res.source).toBe('json');
    expect(res.items[0].title).toBe('json');
    expect(fetchImpl).toHaveBeenCalledTimes(2);
  });

  it('cai para o JSON quando a API lança (backend fora do ar)', async () => {
    const fetchImpl = vi
      .fn()
      .mockRejectedValueOnce(new TypeError('Failed to fetch'))
      .mockResolvedValueOnce(ok({ items: [{ title: 'json' }] }));
    const res = await fetchWithFallback('http://api/highlights', 'data/x.json', { fetch: fetchImpl });
    expect(res.source).toBe('json');
  });

  it('vai direto ao JSON quando não há URL de API', async () => {
    const fetchImpl = vi.fn().mockResolvedValue(ok({ items: [] }));
    const res = await fetchWithFallback('', 'data/x.json', { fetch: fetchImpl });
    expect(res.source).toBe('json');
    expect(fetchImpl).toHaveBeenCalledTimes(1);
    expect(fetchImpl).toHaveBeenCalledWith('data/x.json', { cache: 'no-store' });
  });

  it('lança se o fallback JSON também falhar', async () => {
    const fetchImpl = vi.fn().mockResolvedValue(fail(404));
    await expect(
      fetchWithFallback('', 'data/x.json', { fetch: fetchImpl }),
    ).rejects.toThrow(/Fallback indisponível/);
  });
});

describe('loadEconomyTips', () => {
  it('prioriza API e completa com JSON', async () => {
    const fetchImpl = vi
      .fn()
      .mockResolvedValueOnce({
        ok: true,
        json: () =>
          Promise.resolve({
            disclaimer: 'Conteudo educacional.',
            items: [{ title: 'Dica marketing' }],
          }),
      })
      .mockResolvedValueOnce({
        ok: true,
        json: () =>
          Promise.resolve({
            disclaimer: 'Conteudo educacional.',
            items: [{ title: 'Dica RSS' }, { title: 'Outra RSS' }],
          }),
      });
    const res = await loadEconomyTips('http://api/economy-tips', 'data/economy-tips.json', 3, {
      fetch: fetchImpl,
    });
    expect(res.source).toBe('api+json');
    expect(res.items).toHaveLength(3);
    expect(res.items[0].title).toBe('Dica marketing');
    expect(res.items[0].featured).toBe(true);
    expect(res.disclaimer).toContain('educacional');
  });
});

describe('filterRadarDuplicates', () => {
  it('remove highlights editoriais (link /novidades/ ou externalId -radar)', () => {
    const highlights = [
      { title: 'Artigo editorial', link: 'https://trcongroup.com.br/novidades/artigo-editorial' },
      { title: 'Sinal de mercado', link: 'https://example.com/noticia' },
      { title: 'Legado marketing', link: 'https://x.com', externalId: 'abc-v1-radar' },
    ];
    const filtered = filterRadarDuplicates(highlights);
    expect(filtered).toHaveLength(1);
    expect(filtered[0].title).toBe('Sinal de mercado');
  });
});

describe('fetchRadarHighlights', () => {
  it('cai para JSON quando a API só retorna artigos editoriais', async () => {
    const fetchImpl = vi
      .fn()
      .mockResolvedValueOnce({
        ok: true,
        json: () =>
          Promise.resolve({
            items: [{ title: 'Editorial', link: 'https://trcongroup.com.br/novidades/x' }],
          }),
      })
      .mockResolvedValueOnce({
        ok: true,
        json: () =>
          Promise.resolve({
            items: [{ title: 'Sinal pipeline', link: 'https://example.com/sinal' }],
          }),
      });
    const res = await fetchRadarHighlights('http://api/highlights', 'data/home-highlights.json', {
      fetch: fetchImpl,
    });
    expect(res.source).toBe('json');
    expect(res.items).toHaveLength(1);
    expect(res.items[0].title).toBe('Sinal pipeline');
  });
});

describe('buildHighlightsHtml', () => {
  it('renderiza cards com link seguro e escapa conteúdo', () => {
    const html = buildHighlightsHtml([
      { category: 'IA', title: 'Título <b>x</b>', summary: 'resumo', link: 'https://x.com/a', signal: 'up' },
    ]);
    expect(html).toContain('card');
    expect(html).toContain('card-tag');
    expect(html).toContain('&lt;b&gt;');
    expect(html).toContain('https://x.com/a');
    expect(html).toContain('▲');
  });

  it('estado vazio', () => {
    expect(buildHighlightsHtml([])).toContain('Sem destaques');
  });

  it('não injeta href para URL perigosa', () => {
    const html = buildHighlightsHtml([{ title: 't', link: 'javascript:alert(1)' }]);
    expect(html).not.toContain('javascript:');
  });
});

describe('buildNewsHtml', () => {
  it('renderiza cards com fonte', () => {
    const html = buildNewsHtml([{ title: 'nova', summary: 's', url: 'https://x.com', source: 'Portal' }]);
    expect(html).toContain('card');
    expect(html).toContain('Portal');
    expect(html).toContain('target="_blank"');
  });

  it('usa link interno sem nova aba quando há slug', () => {
    const html = buildNewsHtml([{ title: 'nova', summary: 's', slug: 'nova-slug', source: 'TRCon' }]);
    expect(html).toContain('href="/novidades/nova-slug"');
    expect(html).not.toContain('target="_blank"');
  });

  it('estado vazio', () => {
    expect(buildNewsHtml([])).toContain('Sem novidades');
  });
});
