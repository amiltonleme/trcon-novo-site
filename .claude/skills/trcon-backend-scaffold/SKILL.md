---
name: trcon-backend-scaffold
description: Gera o esqueleto completo de um novo módulo de domínio no backend do TRCon Site (controller/service/repository/mapper/domain/dto + testes), seguindo a arquitetura MVC/SOLID definida em doc/05-BACKEND-ARQUITETURA-MVC.md. Use quando o usuário pedir para criar um novo módulo/domínio no backend (ex. "cria o módulo de highlights", "novo domínio X no backend").
---

# TRCon Backend Scaffold

Gera um novo módulo de domínio dentro de `site/backend/src/main/java/br/com/trcon/site/<dominio>`
e seu espelho de testes em `src/test/java/.../<dominio>`, sempre seguindo
`trcongroup/site/doc/05-BACKEND-ARQUITETURA-MVC.md` como fonte de verdade.

## Antes de gerar

1. Leia `trcongroup/site/doc/05-BACKEND-ARQUITETURA-MVC.md` e
   `trcongroup/site/doc/06-BACKEND-MINIMO-ESPECIFICACAO.md` para confirmar nomes
   de domínio, enums e regras já decididas — não invente campos que contradigam
   essas specs.
2. Pergunte ao usuário (se não estiver claro): nome do domínio, campos da
   entidade, se tem enum de status/tipo, e quais endpoints expõe.

## Estrutura a gerar (por domínio)

```text
<dominio>/
  controller/<Dominio>Controller.java
  service/<Dominio>Service.java            (interface)
  service/<Dominio>ServiceImpl.java
  repository/<Dominio>Repository.java
  mapper/<Dominio>Mapper.java              (MapStruct)
  domain/<Dominio>.java                    (@Entity)
  domain/<Dominio>Status.java              (enum, se aplicável)
  dto/request/<Dominio>CreateRequest.java
  dto/response/<Dominio>CreateResponse.java
```

E o espelho em `test/java/.../<dominio>/`:

```text
service/<Dominio>ServiceImplTest.java      (unitário, Mockito)
mapper/<Dominio>MapperTest.java            (unitário)
controller/<Dominio>ControllerIT.java      (integração, Testcontainers)
```

## Regras não negociáveis ao gerar código

- Controller nunca contém regra de negócio — apenas chama o service e traduz o
  retorno em `ResponseEntity`.
- Service depende apenas de interfaces (repository, outros services).
- Mapper só faz transformação estrutural (usar MapStruct `@Mapper(componentModel = "spring")`).
- Toda classe de `service` e `mapper` nasce com teste unitário; todo endpoint
  nasce com teste de integração — não gerar código de produção sem o teste
  correspondente na mesma sessão.
- DTOs de request usam Bean Validation (`@NotBlank`, `@Email`, etc.) conforme
  as regras funcionais da spec do módulo.
- Seguir a convenção de nomes de `05-BACKEND-ARQUITETURA-MVC.md` à risca.

## Depois de gerar

1. Rodar `mvn test` (ou instruir o usuário a rodar) e confirmar que o novo
   módulo não quebra a suíte existente.
2. Verificar cobertura do módulo novo com a skill/gate `trcon-coverage-gate`
   antes de considerar o módulo pronto.
3. Atualizar `trcongroup/site/doc/06-BACKEND-MINIMO-ESPECIFICACAO.md` se o
   módulo novo introduzir endpoint ou entidade não documentada ainda.
