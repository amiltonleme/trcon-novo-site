---
name: trcon-coverage-gate
description: Roda a suíte de testes do backend TRCon Site (site/backend), gera o relatório JaCoCo e verifica se cada módulo está acima do gate de 80% de cobertura de linha/branch definido em doc/10-TESTES-QUALIDADE.md. Use antes de considerar qualquer entrega de backend "pronta" ou antes de propor merge.
---

# TRCon Coverage Gate

Verifica o critério de qualidade obrigatório de `trcongroup/site/doc/10-TESTES-QUALIDADE.md`:
cobertura ≥ 80% de linha e branch por módulo (`lead`, `highlights`, `news`, `shared`).

## Passos

1. A partir de `trcongroup/site/backend`, rodar:
   ```
   mvn clean verify
   ```
   Isso executa testes unitários, testes de integração (Testcontainers) e gera
   o relatório JaCoCo (`target/site/jacoco/index.html` e `target/site/jacoco/jacoco.xml`).

2. Se o build falhar por causa da regra de cobertura do `jacoco-maven-plugin`
   (`check` goal com mínimo 0.80), **não reduzir o limite configurado**. O
   objetivo é identificar o que falta testar.

3. Ler `target/site/jacoco/jacoco.xml` (ou o HTML) e listar, por módulo:
   - percentual de linha e de branch atual
   - classes/métodos com cobertura abaixo de 80%
   - se possível, quais ramos condicionais (branch) não foram exercitados

4. Para cada gap encontrado, escrever o teste faltante (unitário se for lógica
   de `service`/`mapper`/VO; de integração via Testcontainers se for fluxo de
   `controller`+`repository`) e rodar `mvn clean verify` novamente.

5. Repetir até todos os módulos atingirem ≥ 80% de linha e branch.

## Saída esperada ao final

Um resumo objetivo, por módulo:

```
lead:       linha 92% / branch 85%  -> OK
highlights: linha 78% / branch 70%  -> ABAIXO DO GATE (faltam testes em HighlightServiceImpl.filtrarAtivos)
news:       linha 88% / branch 81%  -> OK
```

Se algum módulo estiver abaixo do gate, a tarefa não está concluída até a
lacuna ser fechada — não reportar a entrega como pronta com o gate vermelho.

## Quando usar

- antes de finalizar qualquer sessão de execução que tenha tocado código de
  `site/backend`
- antes de propor um merge/PR
- sempre que o agent `trcon-qa-reviewer` for acionado
