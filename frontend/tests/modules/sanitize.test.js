import { describe, it, expect } from 'vitest';
import {
  changeClass,
  escapeHtml,
  safeClass,
  safeCssColor,
  safeGradient,
  safePercent,
  safeUrl,
} from '../../assets/modules/sanitize.js';

const BASE = 'https://trcon.com.br/';

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
    expect(safeUrl('/novidades', BASE)).toBe('https://trcon.com.br/novidades');
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
