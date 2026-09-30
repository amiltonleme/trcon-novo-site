import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';

const read = path => readFileSync(resolve(import.meta.dirname, path), 'utf8');

const canonical = read('../../doc/canonical/01-POSICIONAMENTO-INSTITUCIONAL.md');
const knowledge = read('../../backend/src/main/resources/chat/trcon-knowledge.yml');
const site = read('../index.html');
const app = read('../assets/app.js');
const commercialProcess = read('../../doc/23-PROCESSO-COMERCIAL.md');

describe('contrato da fonte institucional', () => {
  it('mantém no YAML exatamente os IDs do registro factual canônico', () => {
    const canonicalIds = [...canonical.matchAll(/^\| `([a-z0-9._-]+)` \|/gm)].map(match => match[1]);
    const knowledgeIds = [...knowledge.matchAll(/^\s*- id: ([a-z0-9._-]+)$/gm)].map(match => match[1]);

    expect(knowledgeIds).toEqual(canonicalIds);
    expect(new Set(knowledgeIds).size).toBe(knowledgeIds.length);
  });

  it('publica idade, estados de produto e situação de carreiras aprovados', () => {
    expect(site).toContain('21 anos');
    expect(site).toContain('Sírius Hub de Inteligência Financeira');
    expect(site).toContain('Produto em beta');
    expect(site).toContain('Sírius Agendamento');
    expect(site).toContain('Sírius Marketing');
    expect(site).toContain('No momento, não há vagas abertas publicadas');
    expect(site).toContain('não há banco de talentos disponível');
  });

  it('publica a etapa 4 sem transformar áreas de interesse em vagas ou coletar candidaturas', () => {
    const careers = site.match(/<div class="page" id="page-carreiras">([\s\S]*?)<\/div>\s*<!-- FOOTER/)[1];

    expect(careers).toContain('21 anos de experiência');
    expect(careers.match(/<article class="career-principle">/g)).toHaveLength(4);
    expect(careers.match(/<details>/g)).toHaveLength(5);
    expect(careers).toContain('A lista apresenta campos de atuação e não representa vagas abertas.');
    expect(careers).toContain('O formulário de contato é destinado a conversas comerciais');
    expect(careers).not.toMatch(/<form|<input|<textarea|type="file"/i);
  });

  it('não publica alegações removidas nem promessa comercial sem aprovação', () => {
    const publicCopy = `${site}\n${app}`;

    expect(publicCopy).not.toMatch(/desde 2005|fundada em 2005/i);
    expect(publicCopy).not.toMatch(/90 dias|vital[ií]cio|vagas limitadas|teste gratuito/i);
    expect(publicCopy).not.toMatch(/Market AI|BI & Analytics|Serviço Ativo/i);
  });

  it('publica a arquitetura e as quatro ofertas completas da etapa 2', () => {
    expect(site).toContain('data-page-link="conteudo"');
    expect(site).toContain('id="page-conteudo"');
    expect(site).toContain('id="page-como-ajudamos"');
    expect(app).toContain("clientes: 'como-ajudamos'");

    for (const offer of [
      'Diagnóstico de IA e automação',
      'Desenvolvimento de MVP ou produto',
      'Modernização e customização',
      'Outsourcing por profissional, célula ou squad',
    ]) {
      expect(site).toContain(offer);
    }
    for (const field of ['Problema', 'Entregáveis', 'Processo', 'Contratação']) {
      expect(site.match(new RegExp(`<dt>${field}</dt>`, 'g'))).toHaveLength(4);
    }
  });

  it('preserva contexto por oferta e registra funil e métricas internas', () => {
    for (const origin of [
      'site-trcon-diagnostico-ia',
      'site-trcon-oferta-mvp',
      'site-trcon-oferta-modernizacao',
      'site-trcon-oferta-outsourcing',
    ]) {
      expect(`${site}\n${app}`).toContain(origin);
      expect(commercialProcess).toContain(origin);
    }
    for (const stage of ['Recepção', 'Qualificação', 'Reunião de entendimento', 'Proposta', 'Acompanhamento']) {
      expect(commercialProcess).toContain(stage);
    }
    for (const metric of ['Visitas por oferta', 'Formulários iniciados', 'Leads qualificados', 'Reuniões', 'Propostas', 'Contratos']) {
      expect(commercialProcess).toContain(metric);
    }
  });

  it('mantém o HTML institucional sem estilos inline e seletores financeiros legados', () => {
    expect(site).not.toMatch(/\sstyle\s*=/i);
    expect(site).not.toContain('market-disclaimer');
    expect(site).toContain('assets/brand/technology-icons.svg#');
  });
});
