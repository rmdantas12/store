# Store API

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](#)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.34.1-4695EB?logo=quarkus&logoColor=white)](#)
[![Release](https://img.shields.io/github/v/release/rmdantas12/store?label=Release)](https://github.com/rmdantas12/store/releases)
[![Docker image](https://img.shields.io/github/v/tag/rmdantas12/store?label=ghcr.io%2Frmdantas12%2Fstore&logo=docker&logoColor=white)](https://github.com/rmdantas12/store/pkgs/container/store)

API de uma loja, construída com Quarkus, seguindo **Arquitetura Hexagonal (Ports & Adapters)**.

## Links rápidos

- **Releases**: `https://github.com/rmdantas12/store/releases`
- **Imagem Docker (GHCR)**: `https://github.com/rmdantas12/store/pkgs/container/store`
- **Keycloak (token)**: `docs/KEYCLOAK.md`

## Autenticação (token obrigatório)

Todas as rotas em `/api/*` exigem um **token Bearer válido**.

- **Como obter token**: veja `docs/KEYCLOAK.md`
- **Como enviar nas requests**:

```http
Authorization: Bearer <access_token>
```

## Padrão de commits

Use mensagens curtas, no formato:

```text
<tipo>: <descrição>
```

Tipos usados no projeto:

- **feat**: nova funcionalidade
- **fix**: correção de bug
- **doc**: documentação (README, docs, comentários essenciais)
- **style**: formatação/estilo (sem mudança de comportamento)
- **chore**: tarefas gerais (ajustes pequenos, manutenção)
- **ci**: pipeline (GitHub Actions, sonar/jacoco, etc.)
- **build**: build/deps (Gradle, plugins, dependências)

Exemplos:

```text
feat: criar CRUD de produtos
fix: ajustar validação de pagamento na venda
doc: documentar uso do token nas APIs
```

## Arquitetura e organização de pacotes (Hexagonal)

Base do código: `src/main/java/com/perinity/store`.

- **`domain/`**: núcleo (regras de negócio, modelos e contratos/ports)
- **`application/`**: casos de uso (orquestra regras do domínio e chama ports de saída)
- **`infrastructure/`**: adaptadores (HTTP, persistência e integrações externas)

Ideia principal:

- O **núcleo** (`domain` + `application`) não depende de web/banco.
- A **infra** depende do núcleo e “pluga” as implementações via **ports**.

## Como rodar localmente

### Subir serviços (Postgres + Keycloak)

```bash
docker compose up -d
```

### Rodar em modo dev

```bash
./gradlew quarkusDev
```

## Build e testes

```bash
./gradlew build
```

## Docker image

A imagem é gerada para o GHCR com o nome:

- `ghcr.io/rmdantas12/store:<tag>`

Veja a lista de versões em: `https://github.com/rmdantas12/store/pkgs/container/store`
