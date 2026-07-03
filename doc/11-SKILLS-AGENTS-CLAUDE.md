# Skills e Agents do Claude Code — TRCon Site

## Objetivo

Documentar as skills e os subagents do Claude Code criados para construir e
manter `trcongroup/site` com consistência arquitetural, mesmo em sessões
diferentes e com contexto zerado a cada nova conversa.

Localização física: `trcongroup/site/.claude/`.

```text
site/.claude/
  skills/
    trcon-backend-scaffold/SKILL.md
    trcon-coverage-gate/SKILL.md
  agents/
    trcon-backend-architect.md
    trcon-qa-reviewer.md
```

## Skills

### `trcon-backend-scaffold`
Gera o esqueleto completo de um novo módulo de domínio no backend
(`controller/service/repository/mapper/domain/dto` + testes), seguindo
[05-BACKEND-ARQUITETURA-MVC.md](./05-BACKEND-ARQUITETURA-MVC.md). Deve ser
invocada sempre que um módulo novo (ex.: `content`, `feature-flags`) for
necessário, para garantir que a estrutura de pastas e nomenclatura sejam
idênticas às dos módulos existentes.

### `trcon-coverage-gate`
Roda `mvn clean verify`, lê o relatório JaCoCo e verifica o gate de 80% de
cobertura por módulo, conforme [10-TESTES-QUALIDADE.md](./10-TESTES-QUALIDADE.md).
Deve ser invocada antes de qualquer entrega de backend ser considerada
concluída.

## Agents (subagents)

### `trcon-backend-architect`
Especialista em revisar/implementar código de `site/backend` respeitando as
camadas MVC e os princípios SOLID definidos em
[05-BACKEND-ARQUITETURA-MVC.md](./05-BACKEND-ARQUITETURA-MVC.md). Acionar antes
de aceitar qualquer módulo novo ou alteração estrutural como pronta.

### `trcon-qa-reviewer`
Especialista em qualidade e cobertura de teste, aplicando o gate de
[10-TESTES-QUALIDADE.md](./10-TESTES-QUALIDADE.md) e avaliando se os testes
existentes validam comportamento real (não só métrica). Acionar antes de
qualquer merge em `site/backend`.

## Fluxo recomendado de uso conjunto

```text
1. Especificar/alterar módulo -> consultar doc/04 e doc/05
2. Gerar código               -> skill trcon-backend-scaffold
3. Revisar arquitetura        -> agent trcon-backend-architect
4. Validar cobertura/qualidade-> skill trcon-coverage-gate + agent trcon-qa-reviewer
5. Só então propor merge/PR
```

## Regra de manutenção

Sempre que uma decisão arquitetural mudar (`05-BACKEND-ARQUITETURA-MVC.md`) ou
o critério de qualidade mudar (`10-TESTES-QUALIDADE.md`), as skills e agents
acima devem ser revisados na mesma sessão — eles citam esses documentos como
fonte de verdade e não devem divergir deles.
