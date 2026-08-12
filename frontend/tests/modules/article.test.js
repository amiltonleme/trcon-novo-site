import { describe, it, expect } from 'vitest';
import {
  parseArticleSlug,
  resolveNewsHref,
  isInternalArticleHref,
  renderArticleBody,
  formatArticleDate,
  buildArticleUrl,
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
