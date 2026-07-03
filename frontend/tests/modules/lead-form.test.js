import { describe, it, expect, vi } from 'vitest';
import {
  LEAD_TYPES,
  buildLeadPayload,
  submitLead,
  mensagemDeErro,
} from '../../assets/modules/lead-form.js';

describe('buildLeadPayload', () => {
  it('monta o payload no contrato do backend e limpa espaços', () => {
    const p = buildLeadPayload({
      nome: '  Ana  ',
      email: ' ana@trcon.com.br ',
      telefone: ' +55 11 90000-0000 ',
      tipoInteresse: 'ALOCACAO_MAO_DE_OBRA',
      mensagem: '  preciso de 2 devs  ',
    });
    expect(p).toEqual({
      nome: 'Ana',
      email: 'ana@trcon.com.br',
      telefone: '+55 11 90000-0000',
      tipoInteresse: 'ALOCACAO_MAO_DE_OBRA',
      mensagem: 'preciso de 2 devs',
      origem: 'site-trcon',
      consentimentoLgpd: true,
    });
  });

  it('usa defaultTipoInteresse quando o valor do form é inválido', () => {
    const p = buildLeadPayload(
      { nome: 'X', email: 'x@y.com', telefone: '1', tipoInteresse: 'HACK' },
      { defaultTipoInteresse: 'CUSTOMIZACAO' },
    );
    expect(p.tipoInteresse).toBe('CUSTOMIZACAO');
  });

  it('cai em PRODUTO quando não há tipo válido nem default', () => {
    const p = buildLeadPayload({ nome: 'X', email: 'x@y.com', telefone: '1' });
    expect(p.tipoInteresse).toBe('PRODUTO');
  });

  it('dobra "uso pretendido" dentro da mensagem', () => {
    const p = buildLeadPayload({
      nome: 'X',
      email: 'x@y.com',
      telefone: '1',
      tipoInteresse: 'PRODUTO',
      mensagem: 'quero testar',
      uso: 'Organização familiar',
    });
    expect(p.mensagem).toBe('quero testar\nUso pretendido: Organização familiar');
  });

  it('mensagem fica undefined quando não há texto nem uso', () => {
    const p = buildLeadPayload({ nome: 'X', email: 'x@y.com', telefone: '1' });
    expect(p.mensagem).toBeUndefined();
  });

  it('respeita origem customizada', () => {
    const p = buildLeadPayload({ nome: 'X', email: 'x@y.com', telefone: '1' }, {
      origem: 'site-trcon-servicos',
    });
    expect(p.origem).toBe('site-trcon-servicos');
  });

  it('expõe os 4 tipos de lead do backend', () => {
    expect(LEAD_TYPES).toEqual([
      'PRODUTO',
      'DESENVOLVIMENTO_SOB_DEMANDA',
      'CUSTOMIZACAO',
      'ALOCACAO_MAO_DE_OBRA',
    ]);
  });
});

describe('submitLead', () => {
  const payload = { nome: 'A', email: 'a@b.com' };

  it('faz POST e retorna o corpo em caso de sucesso', async () => {
    const fakeFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ id: '123', status: 'PENDING' }),
    });
    const result = await submitLead('http://api/leads', payload, { fetch: fakeFetch });

    expect(result).toEqual({ id: '123', status: 'PENDING' });
    expect(fakeFetch).toHaveBeenCalledWith('http://api/leads', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
  });

  it('lança Error com status e code em resposta não-2xx', async () => {
    const fakeFetch = vi.fn().mockResolvedValue({
      ok: false,
      status: 409,
      json: () => Promise.resolve({ code: 'LEAD_DUPLICADO', message: 'dup' }),
    });

    await expect(submitLead('http://api/leads', payload, { fetch: fakeFetch })).rejects.toMatchObject(
      { status: 409, code: 'LEAD_DUPLICADO', message: 'dup' },
    );
  });

  it('propaga TypeError quando o backend está fora do ar', async () => {
    const fakeFetch = vi.fn().mockRejectedValue(new TypeError('Failed to fetch'));
    await expect(
      submitLead('http://api/leads', payload, { fetch: fakeFetch }),
    ).rejects.toBeInstanceOf(TypeError);
  });
});

describe('mensagemDeErro', () => {
  it('mensagem de conexão para TypeError (backend fora do ar)', () => {
    expect(mensagemDeErro(new TypeError('x'))).toMatch(/conectar ao servidor/i);
  });

  it('mensagem específica para lead duplicado', () => {
    const err = Object.assign(new Error('dup'), { code: 'LEAD_DUPLICADO' });
    expect(mensagemDeErro(err)).toMatch(/já recebemos um cadastro/i);
  });

  it('usa a mensagem do erro quando disponível', () => {
    expect(mensagemDeErro(new Error('Payload inválido.'))).toBe('Payload inválido.');
  });
});
