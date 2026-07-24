import { describe, it, expect, vi } from 'vitest';
import {
  extractItems,
  fetchWithFallback,
  buildHighlightsHtml,
  buildNewsHtml,
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

describe('buildHighlightsHtml', () => {
  it('renderiza cards com link seguro e escapa conteúdo', () => {
    const html = buildHighlightsHtml([
      { category: 'IA', title: 'Título <b>x</b>', summary: 'resumo', link: 'https://x.com/a', signal: 'up' },
    ]);
    expect(html).toContain('IA');
    expect(html).toContain('&lt;b&gt;'); // escapado
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
  it('renderiza itens com fonte', () => {
    const html = buildNewsHtml([{ title: 'nova', summary: 's', url: 'https://x.com', source: 'Portal' }]);
    expect(html).toContain('nova');
    expect(html).toContain('Portal');
    expect(html).toContain('https://x.com');
  });

  it('estado vazio', () => {
    expect(buildNewsHtml([])).toContain('Sem novidades');
  });
});
