# Blog Platform API

API REST de uma plataforma de publicação com usuários, autenticação, posts, tags, comentários, curtidas e relacionamento entre perfis.

## Tecnologias

- Java 21 e Spring Boot 4.1
- Spring Security e JWT
- Spring Data JPA e PostgreSQL
- Bean Validation
- SpringDoc OpenAPI
- Docker e Docker Compose
- Lombok e Maven

## Funcionalidades

- Cadastro e login com JWT
- Consulta e atualização do próprio perfil
- Alteração de senha e exclusão da conta
- Seguir e deixar de seguir usuários
- Criação, consulta e exclusão de posts
- Busca por título ou conteúdo
- Filtro de posts por tag e autor
- Paginação nas listagens
- Curtir e descurtir posts
- Criar, editar, listar e excluir comentários
- Autorização do proprietário para alterar posts e comentários
- Tratamento centralizado de erros

## Endpoints principais

Todos os endpoints usam o prefixo `/api/v1`.

| Recurso | Exemplos |
|---|---|
| Autenticação | `POST /auth/register`, `POST /auth/login` |
| Usuários | `GET /users/{id}`, `GET /users/me`, `POST /users/me`, `PATCH /users/me/password` |
| Relacionamentos | `POST /users/{username}/followers`, `DELETE /users/{username}/followers` |
| Posts | `POST /posts`, `GET /posts`, `GET /posts/{id}`, `DELETE /posts/{id}` |
| Busca e tags | `GET /posts/search`, `GET /posts/tags/{tagName}` |
| Curtidas | `POST /posts/{id}/likes`, `DELETE /posts/{id}/likes` |
| Comentários | `POST /posts/{id}/comments`, `GET /posts/{id}/comments`, `PUT /comments/{id}`, `DELETE /comments/{id}` |

Envie o JWT nos endpoints protegidos:

```http
Authorization: Bearer <token>
```

## Execução com Docker

Copie o arquivo de exemplo e configure as variáveis:

```bash
cp .env.example .env
docker compose up --build
```

Variáveis utilizadas:

```dotenv
DB_USER=postgres
DB_PASS=postgres
DB_NAME=personal_blog
JWT_KEY=uma-chave-com-pelo-menos-32-caracteres
EXPIRATION_JWT=86400000
```

## Execução local

Com Java 21, Maven e PostgreSQL configurados:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/personal_blog
./mvnw spring-boot:run
```

## Documentação da API

Com a aplicação em execução, a interface do OpenAPI fica disponível em:

```text
http://localhost:8080/swagger-ui/index.html
```

## Testes

```bash
./mvnw test
```

## Status

Projeto em desenvolvimento. As próximas melhorias incluem migrations com Flyway, testes unitários e de integração e maior cobertura das regras de autorização.
