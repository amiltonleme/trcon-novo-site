import { describe, it, expect } from 'vitest';
import {
  changeClass,
  escapeHtml,
  safeClass,
  safeCssColor,
  safeGradient,
  safeHttpsImageUrl,
  safePercent,
  safeUrl,
  videoEmbedSrc,
  localizeSiteHref,
  isHtmlArticleBody,
  sanitizeArticleHtml,
} from '../../assets/modules/sanitize.js';

const BASE = 'https://trcongroup.com.br/';

describe('changeClass', () => {
  it('mapeia direções conhecidas', () => {
    expect(changeClass('up')).toBe('chg up');
    expect(changeClass('down')).toBe('chg dn');
  });

  it('usa fallback para direção desconhecida ou ausente', () => {
    expect(changeClass('flat')).toBe('chg');
    expect(changeClass(undefined)).toBe('chg');
  });
});

describe('escapeHtml', () => {
  it('escapa caracteres perigosos', () => {
    expect(escapeHtml('<script>"x"&\'y\'')).toBe(
      '&lt;script&gt;&quot;x&quot;&amp;&#39;y&#39;',
    );
  });

  it('trata null/undefined como string vazia', () => {
    expect(escapeHtml(null)).toBe('');
    expect(escapeHtml(undefined)).toBe('');
  });
});

describe('safeClass', () => {
  it('aceita classes simples', () => {
    expect(safeClass('ia-radar_1', 'fb')).toBe('ia-radar_1');
  });

  it('rejeita valores com caracteres inválidos e usa fallback', () => {
    expect(safeClass('a b', 'fb')).toBe('fb');
    expect(safeClass('<x>', 'fb')).toBe('fb');
    expect(safeClass('', 'fb')).toBe('fb');
  });
});

describe('safeUrl', () => {
  it('aceita http, https e mailto', () => {
    expect(safeUrl('https://x.com/a', BASE)).toBe('https://x.com/a');
    expect(safeUrl('mailto:a@b.com', BASE)).toBe('mailto:a@b.com');
  });

  it('rejeita protocolos perigosos', () => {
    expect(safeUrl('javascript:alert(1)', BASE)).toBe('');
  });

  it('resolve caminho relativo contra a base', () => {
    expect(safeUrl('/novidades', BASE)).toBe('https://trcongroup.com.br/novidades');
    // valor vazio/nulo resolve para a própria base (mesmo comportamento do site atual)
    expect(safeUrl(null, BASE)).toBe(BASE);
  });
});

describe('safePercent', () => {
  it('limita ao intervalo 0..100', () => {
    expect(safePercent(50)).toBe(50);
    expect(safePercent(-10)).toBe(0);
    expect(safePercent(150)).toBe(100);
  });

  it('retorna 0 para valores não numéricos', () => {
    expect(safePercent('abc')).toBe(0);
    expect(safePercent(NaN)).toBe(0);
  });
});

describe('safeCssColor', () => {
  it('aceita hex e var(--token)', () => {
    expect(safeCssColor('#fff')).toBe('#fff');
    expect(safeCssColor('#12ab34')).toBe('#12ab34');
    expect(safeCssColor('var(--gold)')).toBe('var(--gold)');
  });

  it('usa fallback para valores inválidos', () => {
    expect(safeCssColor('red; background:url(x)')).toBe('var(--text3)');
    expect(safeCssColor('', '#000')).toBe('#000');
  });
});

describe('safeGradient', () => {
  it('aceita linear-gradient válido', () => {
    const g = 'linear-gradient(135deg,#1a2535,#0d1219)';
    expect(safeGradient(g)).toBe(g);
  });

  it('usa fallback para valor inválido', () => {
    expect(safeGradient('url(javascript:x)')).toBe(
      'linear-gradient(135deg,#1a2535,#0d1219)',
    );
  });
});

describe('safeHttpsImageUrl', () => {
  it('aceita apenas https absoluto', () => {
    expect(safeHttpsImageUrl('https://images.unsplash.com/photo')).toContain('unsplash');
    expect(safeHttpsImageUrl('http://example.com/a.jpg')).toBe('');
    expect(safeHttpsImageUrl('/local.jpg')).toBe('');
  });

  it('converte página Unsplash em URL de download', () => {
    const page =
      'https://unsplash.com/pt-br/fotografias/uma-pessoa-segurando-um-telefone-celular-na-frente-de-um-grafico-de-acoes-K5mPtONmpHM';
    expect(safeHttpsImageUrl(page)).toBe(
      'https://unsplash.com/photos/K5mPtONmpHM/download?force=true&w=1600',
    );
  });
});

describe('localizeSiteHref', () => {
  it('em local reescreve domínio prod para path relativo', () => {
    expect(
      localizeSiteHref('https://trcongroup.com.br/novidades/meu-slug', {
        hostname: '127.0.0.1',
      }),
    ).toBe('/novidades/meu-slug');
    expect(
      localizeSiteHref('https://www.trcongroup.com.br/', { hostname: 'localhost' }),
    ).toBe('/');
  });

  it('em prod mantém URL absoluta', () => {
    expect(
      localizeSiteHref('https://trcongroup.com.br/novidades/x', {
        hostname: 'trcongroup.com.br',
      }),
    ).toBe('https://trcongroup.com.br/novidades/x');
  });
});

describe('isHtmlArticleBody', () => {
  it('detecta corpo iniciado por tag de bloco', () => {
    expect(isHtmlArticleBody('<h2>Título</h2><p>Texto</p>')).toBe(true);
    expect(isHtmlArticleBody('  <p>Texto</p>')).toBe(true);
  });

  it('não detecta markdown-lite comum', () => {
    expect(isHtmlArticleBody('Linha 1\n\nLinha <b>2</b>')).toBe(false);
    expect(isHtmlArticleBody('**negrito** e texto')).toBe(false);
    expect(isHtmlArticleBody('')).toBe(false);
  });
});

describe('sanitizeArticleHtml', () => {
  it('mantém tags da allowlist e atributos seguros', () => {
    const html = sanitizeArticleHtml(
      '<h2>Título</h2><p>Texto <strong>forte</strong> e <a href="https://x.com/a">link</a>.</p>',
    );
    expect(html).toContain('<h2>Título</h2>');
    expect(html).toContain('<strong>forte</strong>');
    expect(html).toContain('<a href="https://x.com/a" rel="noopener noreferrer">link</a>');
  });

  it('remove script/style e o respectivo conteúdo', () => {
    const html = sanitizeArticleHtml('<p>a</p><script>alert(1)</script><style>*{}</style><p>b</p>');
    expect(html).toBe('<p>a</p><p>b</p>');
  });

  it('remove tags fora da allowlist preservando o texto', () => {
    const html = sanitizeArticleHtml('<div class="x">Bloco</div>');
    expect(html).toBe('Bloco');
  });

  it('descarta atributos perigosos e mantém apenas href/src sanitizados', () => {
    const html = sanitizeArticleHtml('<p onclick="alert(1)">Texto</p><a href="javascript:alert(1)">x</a>');
    expect(html).not.toContain('onclick');
    expect(html).not.toContain('javascript:');
    expect(html).toContain('<p>Texto</p>');
  });

  it('remove img sem https e mantém img https com alt escapado', () => {
    expect(sanitizeArticleHtml('<img src="http://x.com/a.jpg" alt="a">')).toBe('');
    const html = sanitizeArticleHtml('<img src="https://images.unsplash.com/a.jpg" alt="Foo & Bar">');
    expect(html).toContain('src="https://images.unsplash.com/a.jpg"');
    expect(html).toContain('alt="Foo &amp; Bar"');
  });
});

describe('videoEmbedSrc', () => {
  it('converte YouTube e Vimeo', () => {
    expect(videoEmbedSrc('https://www.youtube.com/watch?v=dQw4w9WgXcQ')).toBe(
      'https://www.youtube.com/embed/dQw4w9WgXcQ',
    );
    expect(videoEmbedSrc('https://youtu.be/dQw4w9WgXcQ')).toBe(
      'https://www.youtube.com/embed/dQw4w9WgXcQ',
    );
    expect(videoEmbedSrc('https://vimeo.com/123456789')).toBe(
      'https://player.vimeo.com/video/123456789',
    );
    expect(videoEmbedSrc('https://example.com/video')).toBeNull();
  });
});
