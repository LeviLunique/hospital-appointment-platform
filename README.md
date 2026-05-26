# Hospital Appointment Platform

> **Tech Challenge - Fase 3 | Pos Tech FIAP - Arquitetura e Desenvolvimento Java**
>
> Autor: **Levi Lunique Izidio da Silva**
> Matricula: **RM370139**

Backend hospitalar modular para o Tech Challenge Fase 3, implementado com Java 25, Spring Boot 3.5.7, Lombok, PostgreSQL, Kafka, Kafka UI, GraphQL e Spring Security com autenticacao JWT (HS256).

## Arquitetura

- `scheduling-service`: API REST para criar, alterar e registrar historico clinico de consultas. Tambem expoe os endpoints `POST /api/v1/auth/login` e `POST /api/v1/users` para autenticacao e cadastro de usuarios.
- `history-service`: API GraphQL para historico completo e consultas futuras. Valida o JWT emitido pelo scheduling-service usando o mesmo segredo.
- `notification-service`: consumidor Kafka que envia lembretes e audita processamento.
- `shared-kernel`: contratos compartilhados de eventos, roles, status, topicos Kafka e infraestrutura de seguranca JWT (`JwtService`, `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint`).

Fluxo principal:

1. Usuario chama `POST /api/v1/auth/login` no scheduling-service e recebe um token JWT.
2. Medico ou enfermeiro usa o token para criar/alterar consultas via `Authorization: Bearer <token>`.
3. O servico publica eventos Kafka nos topicos `hospital.consultas.criadas.v1` e `hospital.consultas.alteradas.v1`.
4. `history-service` consome os eventos e atualiza a projecao do historico exposta via GraphQL.
5. Paciente, medico ou enfermeiro consulta o GraphQL do history-service usando o mesmo token JWT.
6. `notification-service` consome o evento, envia lembrete para consultas futuras e registra auditoria.

## Seguranca JWT

- **Algoritmo**: HS256 (JJWT 0.11.5).
- **Claims**: `sub` (username), `role` (`MEDICO`, `ENFERMEIRO` ou `PACIENTE`), `pacienteId` (apenas para PACIENTE), `iat`, `exp`.
- **Expiracao**: 1 hora por padrao (configuravel via `JWT_EXPIRATION_MS`).
- **Segredo compartilhado**: `JWT_SECRET` no `.env` (base64). Ambos os servicos validam tokens com o mesmo segredo - troque em producao.
- **Stateless**: nenhum servico mantem sessao; o token carrega identidade e autorizacoes.
- **Pontos de validacao**: `JwtAuthenticationFilter` (no shared-kernel) intercepta as requisicoes HTTP - vale para REST e GraphQL, ja que o filtro roda antes do processamento da query.

## Execucao Local

### Configurando o `.env`

O Docker Compose carrega automaticamente um arquivo `.env` na raiz do projeto, que centraliza portas, credenciais locais, URLs JDBC, nomes de containers, topicos Kafka e configuracao JWT. Esse arquivo **nao e versionado** (esta no `.gitignore` por conter senhas e o `JWT_SECRET`).

Use o template `.env.example` como base:

```bash
cp .env.example .env
```

Em ambiente local nao e necessario editar nada para funcionar. Para outros ambientes, troque pelo menos:

- `JWT_SECRET` (gere com `openssl rand -base64 48`)
- `SCHEDULING_DB_PASSWORD`, `HISTORY_DB_PASSWORD`, `NOTIFICATION_DB_PASSWORD`
- Senhas dos seed users (precisam casar com os hashes BCrypt das migrations Flyway; em producao prefira cadastrar usuarios via `POST /api/v1/users`)

### Subindo a stack

```bash
docker compose up --build
```

Portas:

- Scheduling REST: `http://localhost:8080/api/v1`
- Scheduling Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Scheduling OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- History GraphQL: `http://localhost:8081/graphql`
- Notification health: `http://localhost:8082/actuator/health`
- Kafka externo: `localhost:9094`
- Kafka UI: `http://localhost:8090`
- PostgreSQL scheduling: `localhost:5432`
- PostgreSQL history: `localhost:5433`
- PostgreSQL notification: `localhost:5434`

## Usuarios Seed

| Perfil | Usuario | Senha |
| --- | --- | --- |
| Medico | `medico` | `Medico@123` |
| Enfermeiro | `enfermeiro` | `Enfermeiro@123` |
| Paciente | `paciente` | `Paciente@123` |

IDs usados pela collection:

- `paciente_id`: `7a3f2f0a-2c1f-4f0a-9368-2d9dbd6e8e44`
- `medico_id`: `4d2f88ef-8b8a-4d9a-a96f-808b1774bc2f`

## Endpoints

Base scheduling: `http://localhost:8080/api/v1`

### Autenticacao e cadastro (publicos)

- `POST /auth/login`: recebe `{ "username", "senha" }`, retorna `{ "token", "tipo", "expiraEmMs", "username", "role", "pacienteId" }`.
- `POST /users`: cria novo usuario (`MEDICO`, `ENFERMEIRO` ou `PACIENTE`). Para `PACIENTE`, pode informar `pacienteId` ou deixar o servico gerar.

### Consultas (precisam de Bearer)

- `POST /consultas`: cria consulta. Roles: `MEDICO`, `ENFERMEIRO`.
- `PATCH /consultas/{consultaId}`: altera consulta. Roles: `MEDICO`, `ENFERMEIRO`.
- `PATCH /consultas/{consultaId}/historico`: edita observacoes clinicas. Role: `MEDICO`.

### Documentacao interativa

- Swagger UI: `http://localhost:8080/swagger-ui/index.html` (clique no cadeado "Authorize" e cole o token JWT)
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Codigos de erro

- `201 Created`: consulta criada
- `200 OK`: alteracao bem-sucedida
- `400 Bad Request`: payload invalido
- `401 Unauthorized`: token ausente, invalido ou expirado, ou credenciais erradas no login
- `403 Forbidden`: token valido mas role nao permite a operacao
- `409 Conflict`: tentativa de cadastrar username ja existente

### Exemplo de uso

```bash
# 1. Faz login e captura o token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"medico","senha":"Medico@123"}' | jq -r .token)

# 2. Cria consulta usando o token
curl -X POST http://localhost:8080/api/v1/consultas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "7a3f2f0a-2c1f-4f0a-9368-2d9dbd6e8e44",
    "medicoId": "4d2f88ef-8b8a-4d9a-a96f-808b1774bc2f",
    "dataHoraInicio": "2026-06-10T14:00:00-03:00",
    "dataHoraFim": "2026-06-10T14:30:00-03:00",
    "motivo": "Retorno clinico"
  }'

# 3. Consulta historico GraphQL com o mesmo token
curl -X POST http://localhost:8081/graphql \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"query":"{ historicoPaciente(pacienteId: \"7a3f2f0a-2c1f-4f0a-9368-2d9dbd6e8e44\", filtro: TODAS) { id dataHoraInicio status } }"}'
```

## GraphQL

Endpoint: `POST http://localhost:8081/graphql` (precisa de `Authorization: Bearer <token>`)

Query de historico:

```graphql
query HistoricoPaciente($pacienteId: ID!, $filtro: HistoricoFiltro) {
  historicoPaciente(pacienteId: $pacienteId, filtro: $filtro) {
    id
    pacienteId
    medicoId
    dataHoraInicio
    status
    motivo
    observacoesClinicas
  }
}
```

Filtros:

- `TODAS`
- `FUTURAS`

Pacientes autenticados podem acessar apenas suas proprias consultas (validado a partir do claim `pacienteId` do JWT). Medicos e enfermeiros podem consultar qualquer paciente.

## Postman e Newman

Arquivos:

- `postman/tech-challenge-fase-3.postman_collection.json`
- `postman/tech-challenge-fase-3.local.postman_environment.json`

A collection contem um folder `Auth` com requisicoes de login para os tres perfis. Cada login salva o token nas variaveis `token_medico`, `token_enfermeiro` e `token_paciente`, usadas pelos demais requests via `Authorization: Bearer {{token_xxx}}`. Execute os logins primeiro (Run > Auth folder) ou rode a collection inteira em ordem.

Com o ambiente rodando:

```bash
docker compose up --build
./scripts/run-postman.sh
```

O script usa `postman/newman:5-alpine`, detecta a rede do Compose pelo container `scheduling-service` e injeta as URLs internas dos servicos.
Ele tambem carrega o `.env` automaticamente, preservando overrides informados no shell.

Overrides disponiveis:

```bash
BASE_URL_SCHEDULING="http://localhost:8080/api/v1" \
BASE_URL_HISTORY="http://localhost:8081" \
BASE_URL_NOTIFICATION="http://localhost:8082" \
./scripts/run-postman.sh
```

Em Apple Silicon, se quiser fixar plataforma:

```bash
NEWMAN_PLATFORM=linux/arm64/v8 ./scripts/run-postman.sh
```

## Testes

Execute a suite Maven:

```bash
mvn test
```

Para empacotar tudo:

```bash
mvn clean package
```

## Variaveis Principais

- `SCHEDULING_DATASOURCE_URL`, `HISTORY_DATASOURCE_URL`, `NOTIFICATION_DATASOURCE_URL`
- `SCHEDULING_DB_USER`, `HISTORY_DB_USER`, `NOTIFICATION_DB_USER`
- `SCHEDULING_DB_PASSWORD`, `HISTORY_DB_PASSWORD`, `NOTIFICATION_DB_PASSWORD`
- `KAFKA_INTERNAL_BOOTSTRAP_SERVERS`
- `KAFKA_TOPIC_CONSULTA_CRIADA`, `KAFKA_TOPIC_CONSULTA_ALTERADA`, `KAFKA_TOPIC_CONSULTA_DLT`
- `JWT_SECRET` (base64, compartilhado entre scheduling-service e history-service)
- `JWT_EXPIRATION_MS` (default 3600000 = 1h)
