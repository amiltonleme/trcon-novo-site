# TRCONGROUP Site — Backend

Backend do site institucional, desenvolvido em Java 21, Spring Boot 3.5,
PostgreSQL e Flyway, seguindo arquitetura modular MVC.

## Execução local

Pré-requisito: PostgreSQL disponível em `localhost:5434`, database
`trcon_site`, usuário e senha `trcon`. O profile `dev` já usa esses valores.

```powershell
cd C:\Trcongroup\projetos\site\backend
.\mvnw.cmd spring-boot:run
```

O backend sobe em `http://127.0.0.1:8081`. Valide com:

```powershell
Invoke-RestMethod http://127.0.0.1:8081/actuator/health
```

No profile `dev`, o assistente institucional fica habilitado em modo stub. Ele
responde pela base factual local sem chave DeepSeek e sem consumo externo. As
configurações de produção permanecem desligadas por padrão.

## Módulos principais

- `lead`: formulário comercial e notificação
- `highlights`: Radar de IA e tecnologia
- `news`: novidades, artigos SSR, sitemap e feed
- `chat`: assistente institucional, rate limit, orçamento e base factual

## Erro `Failed to start bean 'webServerStartStop'`

Esse erro normalmente indica que a porta 8081 já está sendo usada por outra
instância do backend. Localize o processo:

```powershell
Get-NetTCPConnection -State Listen |
  Where-Object LocalPort -eq 8081 |
  Select-Object LocalAddress, LocalPort, OwningProcess
```

Confira o processo antes de encerrá-lo:

```powershell
Get-Process -Id <OwningProcess>
```

Encerre somente uma instância que você reconheça como execução anterior:

```powershell
Stop-Process -Id <OwningProcess>
```

Depois execute novamente `.\mvnw.cmd spring-boot:run`. Se precisar manter duas
instâncias, defina outra porta na sessão:

```powershell
$env:PORT="8082"
.\mvnw.cmd spring-boot:run
```

## Testes

```powershell
.\mvnw.cmd test
```

Os testes de integração usam Testcontainers e exigem Docker Desktop ativo. Os
testes unitários do chat incluem uma verificação do wiring dos beans para evitar
regressão nos construtores de `ChatRateLimiter` e `DeepSeekChatClient`.

Documentação relacionada:

- [`13-AMBIENTE-LOCAL-TESTES.md`](../doc/13-AMBIENTE-LOCAL-TESTES.md)
- [`21-CHAT-IA-DEEPSEEK.md`](../doc/21-CHAT-IA-DEEPSEEK.md)
- [`22-PLANO-REPOSICIONAMENTO-LIMPEZA.md`](../doc/22-PLANO-REPOSICIONAMENTO-LIMPEZA.md)
