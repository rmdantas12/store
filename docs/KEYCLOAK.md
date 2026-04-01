# Keycloak (Docker Compose)

Este projeto inclui um Keycloak para autenticação (OpenID Connect) via `docker-compose`.

## Subir serviços

```bash
docker compose up -d
```

Keycloak: `http://localhost:8081`

## Acessar o Keycloak

- **Admin Console**: `http://localhost:8081`
- **Usuário admin**: `admin`
- **Senha admin**: `admin`

O realm `store` é importado automaticamente a partir de `keycloak/realm-store.json`.

## Realm / Client / Usuários

- **Realm**: `store`
- **Client (confidential)**: `store-api`
  - **Client secret**: `store-secret`
  - **Grant**: password (Direct Access Grants) habilitado
- **Usuário de exemplo**:
  - `seller` / `seller` (role: `seller`)

## Obter token via curl

```bash
curl -X POST "http://localhost:8081/realms/store/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=store-api" \
  -d "client_secret=store-secret" \
  -d "username=seller" \
  -d "password=seller"
```

O `access_token` retornado pode ser usado como:

```http
Authorization: Bearer <access_token>
```

## Postman

Importe:
- `postman/store.postman_collection.json`
- `postman/store.local.postman_environment.json`

Passos sugeridos:
1. Selecionar o environment `Store Local`
2. Executar **Auth - Get token (seller)** ou **Auth - Get token (admin)**
3. Copiar o `accessToken` do environment e usar nas chamadas da aplicação (quando aplicável).

## Usar o token na aplicação

As rotas `/api/*` exigem um token válido. Inclua o header em todas as requests:

```http
Authorization: Bearer <access_token>
```

Exemplo com `curl`:

```bash
curl -X GET "http://localhost:8080/api/customers" \
  -H "Authorization: Bearer <access_token>"
```
