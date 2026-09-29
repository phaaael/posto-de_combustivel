# Sistema de Abastecimentos

API REST para gerenciamento de combustiveis, bombas e abastecimentos de um posto.

O projeto demonstra uma implementacao profissional com Spring Boot, mantendo o desenho simples e proporcional ao problema: separacao por dominio, DTOs como contrato HTTP, regras de negocio nos services, persistencia com JPA, schema versionado com Flyway, tratamento centralizado de erros e testes automatizados.

## Tecnologias

- Java 21
- Spring Boot 3
- Maven
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- Flyway
- H2 para testes
- springdoc-openapi
- JUnit 5
- Mockito
- MockMvc
- Docker Compose

## Arquitetura

O codigo e organizado por dominio, evitando concentrar tudo em pacotes globais de controller, service e repository.

```text
src/main/java/com/posto/abastecimento/
├── abastecimento/
├── bomba/
├── combustivel/
├── config/
└── exception/
```

Fluxo principal:

```text
HTTP Request
  -> Controller
  -> DTO
  -> Service
  -> Repository
  -> Database
```

Controllers recebem requisicoes, validam entrada com `@Valid` e retornam respostas HTTP. Services concentram regras de negocio, validacoes que dependem do banco e transacoes. Repositories cuidam apenas do acesso aos dados. DTOs definem o contrato da API e evitam expor entidades JPA diretamente.

## Modelo de Dados

### Combustivel

- `id`
- `nome`
- `precoLitro`
- `criadoEm`
- `atualizadoEm`

Regras:

- `nome` e obrigatorio e unico.
- `precoLitro` e obrigatorio e deve ser maior que zero.
- A unicidade tambem e garantida no banco por constraint.

### Bomba

- `id`
- `nome`
- `combustivel`
- `criadoEm`
- `atualizadoEm`

Regras:

- `nome` e obrigatorio e unico.
- `combustivelId` deve referenciar um combustivel existente.
- Nao e permitido excluir combustivel associado a bomba.

### Abastecimento

- `id`
- `bomba`
- `data`
- `litros`
- `precoLitro`
- `valorTotal`
- `criadoEm`

Regras:

- `bombaId`, `data` e `litros` sao obrigatorios.
- `litros` deve ser maior que zero.
- `precoLitro` e `valorTotal` sao calculados no backend.
- O preco aplicado e copiado para o abastecimento no momento do registro.
- Nao e permitido excluir bomba com abastecimentos registrados.

## Historico de Precos

Ao registrar um abastecimento, a aplicacao busca o preco atual do combustivel vinculado a bomba e salva uma copia em `abastecimento.precoLitro`.

Exemplo:

```text
litros: 35.50
precoLitro aplicado: 5.79
valorTotal: 205.55
```

Se o preco do combustivel mudar depois, os abastecimentos antigos continuam com o preco historico aplicado na operacao original. O valor total e calculado com escala monetaria:

```java
valorTotal.setScale(2, RoundingMode.HALF_UP)
```

## Como Executar

### Pre-requisitos

- Java 21
- Docker e Docker Compose

Nao e necessario ter Maven instalado globalmente. O projeto inclui Maven Wrapper.

### Subir o Banco Local

```bash
docker compose up -d
```

O `docker-compose.yml` cria um PostgreSQL local com:

```text
database: posto_abastecimento
user: posto
password: posto
port: 5432
```

### Rodar a Aplicacao

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

A API ficara disponivel em:

```text
http://localhost:8080
```

### Variaveis de Ambiente

As credenciais podem ser sobrescritas por variaveis de ambiente:

```bash
DB_URL=jdbc:postgresql://localhost:5432/posto_abastecimento
DB_USER=posto
DB_PASSWORD=posto
```

## Banco de Dados

O schema e controlado pelo Flyway em:

```text
src/main/resources/db/migration/
```

Migrations:

- `V1__create_combustivel.sql`
- `V2__create_bomba.sql`
- `V3__create_abastecimento.sql`

A aplicacao usa:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Assim, o Hibernate valida o schema, mas nao cria tabelas como estrategia principal. A estrutura vem das migrations.

## Documentacao OpenAPI

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## API

Todos os endpoints usam versionamento em `/api/v1`.

### Combustiveis

| Metodo | Endpoint | Descricao |
| --- | --- | --- |
| `POST` | `/api/v1/combustiveis` | Cria um combustivel |
| `GET` | `/api/v1/combustiveis` | Lista combustiveis com paginacao |
| `GET` | `/api/v1/combustiveis/{id}` | Busca combustivel por ID |
| `PUT` | `/api/v1/combustiveis/{id}` | Atualiza combustivel |
| `DELETE` | `/api/v1/combustiveis/{id}` | Remove combustivel |

Exemplo:

```json
{
  "nome": "Gasolina",
  "precoLitro": 5.79
}
```

### Bombas

| Metodo | Endpoint | Descricao |
| --- | --- | --- |
| `POST` | `/api/v1/bombas` | Cria uma bomba |
| `GET` | `/api/v1/bombas` | Lista bombas com paginacao |
| `GET` | `/api/v1/bombas/{id}` | Busca bomba por ID |
| `PUT` | `/api/v1/bombas/{id}` | Atualiza bomba |
| `DELETE` | `/api/v1/bombas/{id}` | Remove bomba |

Exemplo:

```json
{
  "nome": "Bomba 01",
  "combustivelId": 1
}
```

### Abastecimentos

| Metodo | Endpoint | Descricao |
| --- | --- | --- |
| `POST` | `/api/v1/abastecimentos` | Registra abastecimento |
| `GET` | `/api/v1/abastecimentos` | Lista abastecimentos com paginacao e filtros |
| `GET` | `/api/v1/abastecimentos/{id}` | Busca abastecimento por ID |
| `PUT` | `/api/v1/abastecimentos/{id}` | Atualiza abastecimento |
| `DELETE` | `/api/v1/abastecimentos/{id}` | Remove abastecimento |

Exemplo:

```json
{
  "bombaId": 1,
  "data": "2026-09-29T10:30:00",
  "litros": 35.50
}
```

O cliente nao envia `precoLitro` nem `valorTotal`. Esses valores sao definidos pelo backend.

## Paginacao e Filtros

Listagens aceitam parametros padrao do Spring Data:

```text
page=0
size=20
sort=campo,direcao
```

Exemplos:

```text
GET /api/v1/combustiveis?page=0&size=10
GET /api/v1/bombas?page=0&size=10&sort=nome,asc
GET /api/v1/abastecimentos?page=0&size=20&sort=data,desc
```

Filtros de abastecimentos:

```text
GET /api/v1/abastecimentos?bombaId=1
GET /api/v1/abastecimentos?dataInicio=2026-09-01&dataFim=2026-09-30
GET /api/v1/abastecimentos?bombaId=1&dataInicio=2026-09-01&dataFim=2026-09-30
```

## Exemplos com curl

```bash
curl -X POST http://localhost:8080/api/v1/combustiveis \
  -H "Content-Type: application/json" \
  -d '{"nome":"Gasolina","precoLitro":5.79}'

curl -X POST http://localhost:8080/api/v1/bombas \
  -H "Content-Type: application/json" \
  -d '{"nome":"Bomba 01","combustivelId":1}'

curl -X POST http://localhost:8080/api/v1/abastecimentos \
  -H "Content-Type: application/json" \
  -d '{"bombaId":1,"data":"2026-09-29T10:30:00","litros":35.50}'

curl "http://localhost:8080/api/v1/abastecimentos?bombaId=1&page=0&size=20&sort=data,desc"
```

## Tratamento de Erros

Erros sao padronizados por um `@RestControllerAdvice`.

Recurso nao encontrado:

```json
{
  "timestamp": "2026-09-29T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Bomba nao encontrada",
  "path": "/api/v1/bombas/10",
  "fields": null
}
```

Validacao:

```json
{
  "timestamp": "2026-09-29T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados invalidos",
  "path": "/api/v1/abastecimentos",
  "fields": {
    "litros": "deve ser maior que ou igual a 0.001"
  }
}
```

A API nao retorna stack trace, SQL ou detalhes internos nos erros tratados.

## Testes

Rodar a verificacao completa:

```bash
./mvnw clean verify
```

No Windows:

```bash
mvnw.cmd clean verify
```

O projeto possui testes unitarios dos services com JUnit 5 e Mockito, alem de testes de integracao dos principais endpoints com Spring Boot, MockMvc, Flyway e H2.

Cenarios cobertos incluem criacao de combustivel, duplicidade, alteracao de preco, criacao de bomba, recursos inexistentes, calculo e arredondamento de abastecimento, preservacao de preco historico, regras de exclusao e respostas `400`/`404`.

## Decisoes Tecnicas

### DTOs

Entidades de persistencia nao sao expostas diretamente pela API. Requests e responses usam DTOs, preferencialmente `record`, para manter um contrato HTTP claro.

### Historico de Precos

O preco aplicado e armazenado no abastecimento para preservar o historico. Abastecimentos antigos nao sao recalculados quando o preco do combustivel muda.

### Flyway

O schema e versionado e nao depende da geracao automatica do Hibernate. Isso torna a evolucao do banco explicita e reproduzivel.

### PostgreSQL

PostgreSQL e usado na execucao normal para aproximar o ambiente local de um banco relacional real. H2 fica restrito aos testes automatizados.

### Simplicidade

O projeto evita abstracoes genericas, interfaces sem necessidade, arquitetura hexagonal, mensageria, cache e autenticacao, porque essas escolhas nao sao necessarias para o escopo atual.

## Estrutura de Commits

O historico foi organizado em commits pequenos e relacionados, seguindo mensagens no formato solicitado pelo desafio.
