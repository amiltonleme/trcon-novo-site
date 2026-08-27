import { describe, it, expect } from 'vitest';
import {
  parseArticleSlug,
  resolveNewsHref,
  isInternalArticleHref,
  renderArticleBody,
  formatArticleDate,
  buildArticleUrl,
  buildNewsArticleJsonLd,
} from '../../assets/modules/article.js';

describe('parseArticleSlug', () => {
  it('extrai slug da URL', () => {
    expect(parseArticleSlug('/novidades/como-ia-ajuda-pmes')).toBe('como-ia-ajuda-pmes');
  });

  it('retorna null sem slug', () => {
    expect(parseArticleSlug('/novidades/')).toBeNull();
    expect(parseArticleSlug('/')).toBeNull();
  });
});

describe('resolveNewsHref', () => {
  it('prioriza slug interno', () => {
    expect(resolveNewsHref({ slug: 'meu-artigo', url: 'https://trcongroup.com.br' }))
      .toBe('/novidades/meu-artigo');
  });

  it('mantém URL externa', () => {
    expect(resolveNewsHref({ url: 'https://partner.example.com/post' }))
      .toBe('https://partner.example.com/post');
  });
});

describe('isInternalArticleHref', () => {
  it('detecta caminho relativo e absoluto', () => {
    expect(isInternalArticleHref('/novidades/foo')).toBe(true);
    expect(isInternalArticleHref('https://trcongroup.com.br/novidades/foo')).toBe(true);
    expect(isInternalArticleHref('https://example.com/x')).toBe(false);
  });
});

describe('renderArticleBody', () => {
  it('gera paragrafos escapados', () => {
    const html = renderArticleBody('Linha 1\n\nLinha <b>2</b>');
    expect(html).toContain('<p>Linha 1</p>');
    expect(html).toContain('&lt;b&gt;');
  });

  it('renderiza markdown-lite seguro para negrito e listas', () => {
    const html = renderArticleBody(
      'O desafio\n\n**Estratégias práticas:**\n\n- Definir prioridades claras\n- Medir resultados',
    );
    expect(html).toContain('<p>O desafio</p>');
    expect(html).toContain('<strong>Estratégias práticas:</strong>');
    expect(html).toContain('<ul>');
    expect(html).toContain('<li>Definir prioridades claras</li>');
    expect(html).toContain('<li>Medir resultados</li>');
  });

  it('escapa html cru dentro de listas e negrito markdown', () => {
    const html = renderArticleBody('**Texto <script>x</script>**\n\n- Item <img src=x>');
    expect(html).toContain('<strong>Texto &lt;script&gt;x&lt;/script&gt;</strong>');
    expect(html).toContain('<li>Item &lt;img src=x&gt;</li>');
  });

  it('converte URL YouTube em iframe', () => {
    const html = renderArticleBody('Intro\n\nhttps://www.youtube.com/watch?v=dQw4w9WgXcQ\n\nFim');
    expect(html).toContain('youtube.com/embed/dQw4w9WgXcQ');
    expect(html).toContain('iframe');
  });

  it('renderiza corpo que já vem em HTML (ex.: Sirius Marketing) em vez de escapar as tags', () => {
    const html = renderArticleBody(
      '<h2>Introdução</h2><p>Texto <strong>em negrito</strong>.</p><ul><li>Item 1</li><li>Item 2</li></ul>',
    );
    expect(html).toContain('<h2>Introdução</h2>');
    expect(html).toContain('<p>Texto <strong>em negrito</strong>.</p>');
    expect(html).toContain('<ul><li>Item 1</li><li>Item 2</li></ul>');
    expect(html).not.toContain('&lt;h2&gt;');
  });

  it('sanitiza corpo HTML removendo scripts e tags fora da allowlist', () => {
    const html = renderArticleBody(
      '<h2>Título</h2><script>alert(1)</script><p onclick="alert(1)">Texto</p><div>Bloco</div>',
    );
    expect(html).not.toContain('<script');
    expect(html).not.toContain('alert(1)');
    expect(html).not.toContain('onclick');
    expect(html).toContain('<p>Texto</p>');
    expect(html).toContain('Bloco');
    expect(html).not.toContain('<div>');
  });
});

describe('formatArticleDate', () => {
  it('formata data pt-BR', () => {
    expect(formatArticleDate('2026-07-22T18:00:00Z')).toMatch(/2026/);
  });
});

describe('buildArticleUrl', () => {
  it('monta URL canônica', () => {
    expect(buildArticleUrl('slug-teste')).toBe('https://trcongroup.com.br/novidades/slug-teste');
  });
});

describe('buildNewsArticleJsonLd', () => {
  it('gera NewsArticle com campos SEO', () => {
    const json = buildNewsArticleJsonLd({
      title: 'Título',
      description: 'Desc',
      canonical: 'https://trcongroup.com.br/novidades/titulo',
      cover: 'https://images.unsplash.com/photo-x',
      publishedAt: '2026-08-16T12:00:00Z',
    });
    expect(json['@type']).toBe('NewsArticle');
    expect(json.headline).toBe('Título');
    expect(json.description).toBe('Desc');
    expect(json.image).toEqual(['https://images.unsplash.com/photo-x']);
    expect(json.datePublished).toBe('2026-08-16T12:00:00Z');
  });
});
