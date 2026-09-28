import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';

const read = path => readFileSync(resolve(import.meta.dirname, path), 'utf8');

const canonical = read('../../doc/canonical/01-POSICIONAMENTO-INSTITUCIONAL.md');
const knowledge = read('../../backend/src/main/resources/chat/trcon-knowledge.yml');
const site = read('../index.html');
const app = read('../assets/app.js');

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
    expect(site).toContain('No momento, não há vagas abertas publicadas.');
  });

  it('não publica alegações removidas nem promessa comercial sem aprovação', () => {
    const publicCopy = `${site}\n${app}`;

    expect(publicCopy).not.toMatch(/desde 2005|fundada em 2005/i);
    expect(publicCopy).not.toMatch(/90 dias|vital[ií]cio|vagas limitadas|teste gratuito/i);
    expect(publicCopy).not.toMatch(/Market AI|BI & Analytics|Serviço Ativo/i);
  });
});
