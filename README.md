# ShikenStore

Tienda online de videojuegos construida con Spring Boot + Oracle Cloud DB.

## Stack

- **Backend:** Spring Boot 4, Spring Security, Spring Data JPA
- **BD:** Oracle Autonomous Database (Cloud)
- **API Docs:** Swagger UI (SpringDoc OpenAPI)
- **Views:** Thymeleaf + CSS
- **Deploy:** Docker + Dokploy

## Estructura

```
src/main/java/com/shikenstore/shikenstoreapp/
├── config/          # Security, DataInitializer
├── controller/      # Vistas (Thymeleaf)
│   └── api/         # REST Controllers
├── model/           # Entidades JPA (Product, User, Order, Cart)
├── repository/      # Spring Data repositories
└── service/         # Logica de negocio
```

## API REST

| Recurso | Endpoints |
|---------|-----------|
| Productos | `GET/POST/PUT /api/products` |
| Usuarios | `GET/PUT /api/users` |
| Auth | `POST /api/auth/login`, `/register` |
| Carrito | `GET/POST/PUT/DELETE /api/cart/{userId}` |
| Ordenes | `GET/POST/PUT /api/orders` |

Swagger UI disponible en `/swagger-ui/index.html`

## Correr local

```bash
./mvnw spring-boot:run
```

Requiere wallet de Oracle en `src/main/resources/wallet/` y credenciales en `application.properties`.

## Tests

```bash
./test-api.sh
```

Ejecuta 63 tests contra todos los endpoints de la API.

## Deploy

Docker Compose con 3 env vars: `ORACLE_USERNAME`, `ORACLE_PASSWORD`, `ORACLE_WALLET_BASE64`.

```bash
docker compose up --build
```
