# FashionPlace

An eBay-style Spring Boot MVC marketplace for jewelry and clothing. Users can browse and search listings, add items to a session cart, place orders, sell products, manage a wishlist, contact support, and leave reviews. Administrators can moderate listings and users via a dashboard.

## Team

| Full name                | College email          |
|--------------------------|------------------------|
| Noa Haberer - 209829845  | noahab@edu.jmc.ac.il   |
| Joelle Gharo - 328295423 | joellegh@edu.jmc.ac.il |

## How to run

### 1. Start the database

```bash
docker compose up -d
```

- MariaDB: `localhost:3306`, database **`ex4`**, user `root`, password `password`
- phpMyAdmin: [http://localhost:8081](http://localhost:8081)

### 2. Build the project

```bash
mvnw clean package
```

### 3. Run the application

```bash
mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080).

On first startup with an empty database, Hibernate creates tables (`ddl-auto=update`) and `DataSeeder` / `AdminInitializer` insert sample data.

### 4. Restore from SQL dump (optional)

A snapshot of the sample database is included at [`sql/ex4.sql`](sql/ex4.sql) (schema + seed data: products, users, reviews).

**Command line** (with Docker running):

```bash
docker exec -i mariadb-server mariadb -uroot -ppassword < sql/ex4.sql
```

on windows use
```bash
cmd /c "docker exec -i mariadb-server mariadb -uroot -ppassword < sql\ex4.sql"
```

**phpMyAdmin:** open [http://localhost:8081](http://localhost:8081), select database `ex4`, use **Import** and choose `sql/ex4.sql`.

### 5. Default admin account

| Field    | Value                    |
|----------|--------------------------|
| Username | `admin`                  |
| Password | `password`               |
| Email    | `admin@fashionplace.com`   |
| Role     | `ADMIN`                  |

### 6. Sample seeded users

| Username | Password   | Role  |
|----------|------------|-------|
| `alice`  | `password` | ADMIN |
| `bob`    | `password` | USER  |

## Configuration

Database settings are in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/ex4
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

## Main features

| Area | URL / behaviour |
|------|-----------------|
| Home | `/` |
| Browse & search | `/browse` — filters, autocomplete, recommendations |
| Product detail | `/product/{id}` — reviews, cart, wishlist |
| Cart | `/cart` |
| Checkout | `/checkout` (authenticated) |
| Orders | `/orders` (authenticated) |
| Sell / My products | `/sell`, `/my-products` (authenticated) |
| Wishlist | `/wishlist` (authenticated) |
| Support | `/support` (authenticated) |
| Register / Login | `/register`, `/login` |
| Admin dashboard | `/admin` (ADMIN role) |

Session-scoped beans: `CartBean`, `RecentSearchBean`, `InterestBean`.

## REST API (JSON / binary)

All REST endpoints live in package `com.fashionplace.restApi` under the `/api` prefix.

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/api/products/suggest?q=` | Public | JSON array of product title suggestions |
| `GET` | `/api/products/{id}/image` | Public | Product image bytes (`image/*` or 404) |
| `POST` | `/api/wishlist/toggle?productId=` | Authenticated | JSON `{"saved": true\|false}` |


## Project structure

```
src/main/java/com/fashionplace/
  controller/     MVC pages and form handling
  restApi/        REST JSON/binary endpoints
  service/        Business logic
  repository/     Spring Data JPA (data access)
  model/          JPA entities
  dto/            Form and view DTOs
  config/         Security, seeding, interceptors
  session/        Session-scoped beans
sql/
  ex4.sql         Database dump (schema + sample data)
```

## Troubleshooting

| Problem | Check |
|---------|--------|
| Cannot connect to DB | `docker compose ps` — MariaDB must be running on port 3306 |
| Access denied for DB user | Credentials in `application.properties` match `docker-compose.yml` |
| Port 8080 in use | Stop other apps or change `server.port` |
| CSRF error on POST | Forms must include CSRF token (layout provides meta tags for AJAX) |

