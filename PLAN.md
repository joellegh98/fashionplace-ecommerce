# FashionPlace - Build Plan

An eBay-style Spring Boot MVC marketplace for jewelry & clothing.
Built **bottom-up in tiny steps**: start with the smallest working thing, then add
**one small change per level**. No big jumps. Each level should compile, run, and be
testable before moving on.

> **Golden rule:** Login / registration / real Spring Security come **LAST** (Phase 9).
> Until then we build every page and feature openly, with a temporary "fake current user".

---

## Legend

- Each **Level** = one small change/adjustment. Do them in order.
- `[ ]` = todo. Tick it when the app still runs after the change.
- After every level: run `./mvnw spring-boot:run` and click the affected page.

---

## Phase 0 - Foundation (make it boot and look alive)

**Goal:** The app starts, connects to the DB, and shows a styled Home page. No business logic yet.

- [x] **0.1 - Boot check.** Run `./mvnw clean package` then `./mvnw spring-boot:run`. Confirm it starts on `http://localhost:8080`. (Security will redirect to a login page for now - that's expected.)
  > 📖 **Materials:** `07-SpringMVC.pdf` – Spring Boot features (slide 3: embedded server, no XML config); `@SpringBootApplication` and `SpringApplication.run()` (slides 4, 27).

- [x] **0.2 - Start the database.** `docker compose up -d`. Confirm phpMyAdmin at `http://localhost:8081`.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `application.properties` configuration (slide 4); session store via JDBC (slide 24). `08-JPA.pdf` – Spring Initializer modules: MySQL Driver + Spring Data JPA (slide 23); configure DB credentials in `application.properties` (slide 28).

- [x] **0.3 - Align the DB name to `ex4` (required by the exercise).**
  - In `docker-compose.yml`: `MYSQL_DATABASE: ex4`.
  - In `src/main/resources/application.properties`: change URL to `jdbc:mariadb://localhost:3306/ex4` and set real username/password matching docker-compose.
    > 📖 **Materials:** `07-SpringMVC.pdf` – `application.properties` definitions (slide 4); define custom global params with `@Value` (slide 23); session store JDBC config (slide 24). `08-JPA.pdf` – full `application.properties` config for MySQL: `spring.datasource.url`, `username`, `password`, `driver-class-name`, `spring.jpa.hibernate.ddl-auto=create/update`, `spring.jpa.show-sql=true` (slide 28).

- [x] **0.4 - Temporarily open all pages.** Add a `SecurityConfig` that **permits all requests** (so we can build UI without logging in). This file gets rewritten in Phase 9. Leave a `// TODO: lock down in Phase 9` comment.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Configuration` class and `@Bean` annotation for explicit bean declaration (slides 12, 14); `@SpringBootApplication` / `@EnableAutoConfiguration` must be present for injection to work (slide 13).

- [x] **0.5 - First controller + page.** `HomeController` returns a `home` Thymeleaf view that just says "FashionPlace". Confirm it loads with no login.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@Controller` returning a view / Thymeleaf (slides 9, 11, 12, 13); `@GetMapping` (slide 6); project structure – controllers folder (slides 5, 18); `07-SpringBeans.pdf` – `@Component` / Spring-managed beans, single controller instance shared across threads (slides 5, 6); `07-LongPolling.pdf` – web-server thread pool & single controller instance (slide 2, background awareness). `08-thymeleaf.pdf` – add Thymeleaf dependency in `pom.xml`; add `xmlns:th="http://www.thymeleaf.org"` to every HTML file (slide 2); use `th:text="${variable}"` to display model values (slide 3); standard dialect expressions overview (slide 4).

- [x] **0.6 - Base layout fragment.** Create `templates/fragments/layout.html` (header with logo + nav placeholder, footer). Home page uses it.
  > 📖 **Materials:** `07-SpringMVC.pdf` – templates folder is for Thymeleaf views (slides 5, 18, 29); Thymeleaf as a server-side template engine (slide 13); "no static HTML in /static" rule (slide 18). `08-thymeleaf.pdf` – Thymeleaf fragments to avoid HTML duplication: create fragment files, include with `th:insert` or `th:replace` (slide 11); define a shared `maintemplate.html` and use `th:replace="~{maintemplate :: maintemplate('Page Title')}"` in each view (slide 12).

- [x] **0.7 - Static styling.** Add `static/css/style.css` and link it in the layout. Make Home look clean.
  > 📖 **Materials:** `07-SpringMVC.pdf` – static files served from `/static/` (slides 19, 20); define static folder in `application.properties` (slide 20).

---

## Phase 1 - First entity end-to-end (Product)

**Goal:** Show real products from the database. One entity, one repo, one service, two pages.

- [x] **1.1 - Product entity (minimal).** Fields: `id`, `title`, `description`, `price`. JPA `@Entity`. Let Hibernate create the table (`ddl-auto=update`).
  > 📖 **Materials:** `07-SpringBeans.pdf` – Spring Bean requirements (zero-args constructor, getters/setters, naming convention) (slide 7); `@Component` / managed class (slide 6); `@ElementCollection` note for future list fields (slide 29). `08-JPA.pdf` – `@Entity` creates a DB table; `@Id` + `@GeneratedValue` for auto-generated primary key; all fields become columns automatically; use `@Transient` to exclude a field; `@CreationTimestamp` / `@UpdateTimestamp` for audit fields (slide 3); avoid naming entities `Order` or `User` (SQL reserved words) (slide 3); Roadmap step 1/6: define entity with fields, setters/getters, and validations (slide 25).

- [x] **1.2 - ProductRepository.** `extends JpaRepository<Product, Long>`.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Repository` is a specialization of `@Component` for database operations (slide 6); typical app structure with a `repo` package (slide 24, 27); beans are used for database access (slide 25). `08-JPA.pdf` – `JpaRepository` provides CRUD + JPA flush/batch operations; inherits `CrudRepository` (save, findById, findAll, count, delete, exists) and `PagingAndSortingRepository` (slides 8, 9); Roadmap step 2/6: define interface `extends JpaRepository<Entity, Long>` (slide 26).

- [x] **1.3 - Seed data at startup.** A `DataSeeder` (`CommandLineRunner` bean) inserts ~6 products **only if the table is empty** (so it works on an empty DB).
  > 📖 **Materials:** `07-SpringBeans.pdf` – Bean lifecycle: `@PostConstruct` for init logic (slide 28); `@Component` bean auto-detected by classpath scan (slide 6). `08-JPA.pdf` – initialize application data with `CommandLineRunner`: annotate class with `@Component`, inject repository with `@Autowired`, override `run()` to insert data; `run()` is called after context loads (slide 35).

- [x] **1.4 - Browse page.** `/browse` lists all products as cards. (Controller talks to repo directly for now.)
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@GetMapping` (slide 6); `Model` / `model.addAttribute()` to pass list to view (slides 11, 14); Thymeleaf iterating a list in the template (slide 13); controller returns view name (slide 9). `08-thymeleaf.pdf` – iterate a collection with `th:each="product: ${products}"` and display fields with `th:text="${product.title}"` (slide 10); use `th:text` to render individual attributes (slide 3). `08-JPA.pdf` – `findAll()` returns all rows from the table (slide 9); call `repository.findAll()` in the controller and pass to model (slide 29).

- [x] **1.5 - Product detail page.** `/product/{id}` shows one product's full info.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@PathVariable` for REST-style URL parsing (slide 8); `@GetMapping` with URL pattern `/product/{id}` (slide 6); passing the object to the view via `Model` (slide 11). `08-thymeleaf.pdf` – use `${product.field}` variable expressions to display all object properties (slide 5); use `th:text="${product.price}"` etc. (slide 3). `08-JPA.pdf` – `findById(id)` returns `Optional<Entity>`; call `.get()` or `.orElseThrow()` (slide 9).

- [x] **1.6 - Introduce ProductService.** Move data access out of the controller into `ProductService` (constructor injection). Controller calls the service. (Demonstrates Beans + DI.)
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Service` for business logic beans (slide 6); **constructor injection** (recommended default) (slides 9, 10, 11); `@Autowired` (slide 8); dependency injection concept / Inversion of Control (slides 2, 3, 4); controller → service → repository dependency graph (slide 8 diagram). `08-JPA.pdf` – Roadmap step 2/6: define a `@Service` class to encapsulate operations and separate the controller from the DB interface (slide 26); Roadmap step 3/6: let Spring inject the repository with `@Autowired` (slide 27).

- [x] **1.7 - Nav links.** Add Home / Browse links to the layout header.
  > 📖 **Materials:** `07-SpringMVC.pdf` – Thymeleaf template syntax, `th:href` in layout (slide 13, 29); static folder for linked CSS/JS (slide 19). `08-thymeleaf.pdf` – `@{...}` link expressions: use `th:href="@{/browse}"` for context-aware URLs (slide 7); include links inside a reusable layout fragment (slides 11, 12).

---

## Phase 2 - Search, filter & session state

**Goal:** Make Browse useful and introduce the first session-scoped bean.

- [x] **2.1 - Keyword search.** Add a search box on Browse; `/browse?q=ring` filters by title (repo `findByTitleContainingIgnoreCase`).
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@RequestParam` to bind query-string parameters (slide 7); optional param with `required=false, defaultValue=""` (slide 7); `@GetMapping` (slide 6). `08-JPA.pdf` – Spring Data query creation by method name: `findByTitleContainingIgnoreCase(String q)` — Spring generates the SQL automatically (slides 10, 11); supported keywords include `Containing`, `IgnoreCase`, `And`, `Or` (slide 12).

- [x] **2.2 - Extend Product fields.** Add `category`, `condition`, `status` (e.g. ACTIVE/SOLD), `imageUrl`. Update seed data.
  > 📖 **Materials:** `07-SpringBeans.pdf` – bean member/getter/setter naming convention (slide 7); `@Converter` note for enum-to-DB mapping (slide 29). `08-JPA.pdf` – adding new fields to an `@Entity`: each field becomes a DB column automatically; use `ddl-auto=update` to add columns without dropping data (slide 3).

- [x] **2.3 - Filters.** Add category + min/max price + condition filters to Browse (service builds the query).
  > 📖 **Materials:** `07-SpringMVC.pdf` – receiving all parameters as a `MultiValueMap` or individual `@RequestParam` values (slide 7); `@Service` builds the query (keep controller thin) (slide 9). `08-JPA.pdf` – use `@Query("SELECT p FROM Product p WHERE ...")` for complex filter queries when method-name syntax is insufficient (slide 13); use `@Param` to bind named parameters (slide 13). `08-thymeleaf.pdf` – use `th:if="${condition}"` / `th:unless` to conditionally show active filter badges (slide 8).

- [x] **2.4 - Sorting.** Add sort by price / newest. Keep current sort in the URL.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@RequestParam` with `defaultValue` for sort direction (slide 7); controller passes sort state back via `Model` (slide 14). `08-JPA.pdf` – use `@Query(value = "SELECT p FROM Product p ORDER BY ...")` or `findAll(Sort.by(...))` for dynamic sorting (slide 13).

- [x] **2.5 - RecentSearchBean (`@SessionScope`).** Store last N search keywords in session.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@SessionScope` bean scope (slides 17, 21); `@Bean @SessionScope` in a `@Configuration` class (slide 21); `@Resource(name=...)` injection by bean name (slides 13, 21, 22); bean must implement `Serializable` for HTTP session storage (slide 7); Spring sessions overview (slides 19, 23); why NOT to access `HttpSession` directly (slide 20); `07-LongPolling.pdf` – thread pool & race conditions context (slide 2: each request is a thread, shared bean state must be safe).

- [x] **2.6 - Show recent searches.** Render them as clickable "chips" on the Browse page. (Satisfies the **session** requirement.)
  > 📖 **Materials:** `07-SpringBeans.pdf` – injecting the session bean into the controller with `@Resource` (slide 21); `07-SpringMVC.pdf` – pass session bean data to Thymeleaf via `Model` (slide 14); Thymeleaf iterating a list (slide 13). `08-thymeleaf.pdf` – iterate the recent-search list with `th:each="term : ${recentSearches}"` (slide 10); make each chip a link with `th:href="@{/browse(q=${term})}"` (slide 7).

---

## Phase 3 - Shopping cart (session-based, still no login)

**Goal:** A working cart that lives in the session - no account needed yet.

- [x] **3.1 - CartBean (`@SessionScope`).** Holds a map/list of `{productId, quantity}`.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Bean @SessionScope` in `@Configuration` class (slide 21); session scope definition (slide 17); bean must implement `Serializable` (slide 7); Spring sessions (slide 23); the shopping-cart session problem and why DI solves it (slide 20); `07-LongPolling.pdf` – thread pool: one thread per request, session bean is per-user (slide 2).

- [x] **3.2 - Add to cart.** Button on Product Detail posts to `/cart/add`.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@PostMapping` (slide 6); `@RequestParam` or `@RequestBody` to receive product id + quantity (slides 7, 8); controller redirects after POST to avoid double-submission (slide 9, redirect solves the "double submission" problem). `08-thymeleaf.pdf` – build the "Add to Cart" form with `th:action="@{/cart/add}"`, `th:object`, and hidden `th:field="*{productId}"` inputs (slides 13, 14).

- [x] **3.3 - Cart page.** `/cart` shows items, quantities, line totals, grand total.
  > 📖 **Materials:** `07-SpringMVC.pdf` – controller returning a view with `Model` (slides 9, 11); passing the CartBean's data as model attributes (slide 14); Thymeleaf rendering (slide 13). `08-thymeleaf.pdf` – iterate cart items with `th:each="item : ${cartItems}"` (slide 10); display price, quantity, totals with `th:text="${item.price}"` (slide 3); use `th:switch` / `th:case` for status labels if needed (slide 9).

- [x] **3.4 - Update / remove.** Change quantity and remove items from the cart page.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@PostMapping` / `@DeleteMapping` (slide 6); `@PathVariable` or `@RequestParam` for item id (slides 7, 8); redirect after POST (slide 9). `08-thymeleaf.pdf` – build update/remove forms with `th:action="@{/cart/remove/{id}(id=${item.id})}"` using URL parameter expressions (slide 7); use `th:field` for quantity input (slide 13).

- [x] **3.5 - Cart badge.** Show item count in the header (reads CartBean).
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Resource` injection of the session bean into the controller (slide 21); `07-SpringMVC.pdf` – add count attribute to `Model` so Thymeleaf can render it in the layout fragment (slide 14). `08-thymeleaf.pdf` – display count in the layout fragment with `th:text="${cartCount}"` (slide 3); conditionally show/hide the badge with `th:if="${cartCount > 0}"` (slide 8).

---

## Phase 4 - User & relations (data only, still no auth)

**Goal:** Introduce the User entity and wire relationships. We fake "who is logged in" for now.

- [x] **4.1 - User entity + repo.** Fields: `id`, `username`, `email`, `passwordHash`, `role`, `address`, `createdAt`. Seed an admin user + a couple of regular users.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Repository` for database access bean (slide 6); bean class requirements (slide 7); beans used for database access (slide 25); `@PostConstruct` in DataSeeder for seeding (slide 28). `08-JPA.pdf` – `@Entity` definition with `@Id`, `@GeneratedValue`, `@CreationTimestamp` (slide 3); add validation to fields: `@NotEmpty`, `@Email`, `@NotNull` directly on entity members (slides 14, 15); Roadmap 1/6 for entity structure (slide 25); Roadmap 2/6 for `JpaRepository` interface (slide 26).

- [x] **4.2 - Temporary "current user" helper.** A tiny `CurrentUserProvider` bean that returns a seeded user. (Phase 9 swaps this for the real logged-in user.)
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Component` for a utility bean (slide 6); `@Autowired` / constructor injection (slides 8, 9); singleton scope (default) means one instance shared across all requests (slide 17); `07-SpringMVC.pdf` – `Principal` parameter in controllers (slide 17, "Other params") — this is the real equivalent in Phase 9.

- [x] **4.3 - Product -> seller relation.** `Product` gets `@ManyToOne User seller`. Update seed + show seller name on Product Detail.
  > 📖 **Materials:** `07-SpringBeans.pdf` – bean member with getter/setter (slide 7); `07-SpringMVC.pdf` – pass updated Product object to Thymeleaf view via `Model` (slide 14). `08-JPA.pdf` – relational DB design: store foreign key to avoid data duplication (slide 4); `@ManyToOne` annotation: many products belong to one seller; JPA creates a `seller_id` foreign key column (slide 5); avoid bidirectional relations to prevent infinite loops; if needed mark reverse field `@Transient` (slide 6). `08-thymeleaf.pdf` – display seller name with `th:text="${product.seller.username}"` via chained property access (slide 5).

- [x] **4.4 - Review entity.** `Review` (`rating 1-5`, `comment`, `createdAt`) with `@ManyToOne` to `User` and `Product`. Repo included.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Repository` (slide 6); bean requirements (slide 7); beans are used for database access (slide 25). `08-JPA.pdf` – `@Entity` with two `@ManyToOne` relations (User + Product); each relation adds a foreign key column; JPA may create a join table (slide 5); add `@Min(1)` / `@Max(5)` validation on `rating`, `@NotBlank` on `comment` (slides 14, 15).

- [x] **4.5 - Show reviews.** Display a product's reviews + average rating on Product Detail.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `Model.addAttribute()` / `ModelAndView` to pass a list to the view (slides 14, 16); Thymeleaf iteration (slide 13). `08-thymeleaf.pdf` – iterate reviews with `th:each="review : ${reviews}"` (slide 10); show rating and comment with `th:text="${review.rating}"` (slide 3); conditionally show "No reviews yet" with `th:if="${#lists.isEmpty(reviews)}"` (slide 8).

- [x] **4.6 - Add review form.** Post a review (attributed to the temporary current user).
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@PostMapping` (slide 6); `@RequestBody` or `@RequestParam` to receive form data (slides 7, 8); redirect after POST (slide 9); `07-SpringBeans.pdf` – validation annotations (`@NotBlank`, `@Min`, `@Max`) on the DTO class (slide 26); `@Valid` + `BindingResult` in controller (slide 26); `07-SpringMVC.pdf` – `BindingResult` in Other params (slide 17). `08-thymeleaf.pdf` – build the review form with `th:action="@{/product/{id}/review(id=${product.id})}"`, `th:object="${review}"`, `th:field="*{rating}"`, `th:field="*{comment}"` (slides 13, 14); display field errors inline with `th:if="${#fields.hasErrors('rating')}"` and `th:errors="*{rating}"` (slide 15). `08-JPA.pdf` – validation annotations `@Min`, `@Max`, `@NotBlank` on the Review entity (slides 14, 15); catch errors via `BindingResult` in the controller (slide 19).

- [x] **4.7 - Wishlist entity + repo.** `WishlistItem` (`createdAt`) with `@ManyToOne User owner` and `@ManyToOne Product product`. Repo: `findByOwner(User)`, `existsByOwnerAndProduct(User, Product)`. (7th entity.)
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Repository` (slide 6); bean requirements (slide 7); beans for database access (slide 25). `08-JPA.pdf` – `@Entity` with two `@ManyToOne` relations (User + Product); `@CreationTimestamp` on `createdAt` (slide 3); query methods `findByOwner(...)` and `existsByOwnerAndProduct(...)` (slides 10, 11); Roadmap 1/6 for entity structure (slide 25); Roadmap 2/6 for `JpaRepository` interface (slide 26).

- [x] **4.8 - Wishlist add/remove + page.** "Save" button on Product Detail (and Browse cards) posts to `/wishlist/add`; `/wishlist` lists saved products; remove action. Attributed to the temporary current user.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@GetMapping` / `@PostMapping` (slide 6); `@RequestParam` for product id (slide 7); redirect after POST (slide 9); `Model.addAttribute(list)` (slide 14). `07-SpringBeans.pdf` – `@Service` for wishlist logic (slide 6); constructor injection (slides 9, 10); inject `CurrentUserProvider` (slide 6). `08-thymeleaf.pdf` – wishlist form with `th:action="@{/wishlist/add}"` and hidden `productId` (slides 13, 14); iterate saved items with `th:each="item : ${wishlistItems}"` (slide 10); remove link with `th:href="@{/wishlist/remove/{id}(id=${item.id})}"` (slide 7). `08-JPA.pdf` – `save(wishlistItem)` and `deleteById(id)` (slide 9).

---

## Phase 5 - Orders & checkout

**Goal:** Turn a cart into a persisted order. Introduces transactions.

- [x] **5.1 - Order + OrderItem entities.** `Order` (`totalPrice`, `status`, `createdAt`, `shippingAddress`) `@ManyToOne buyer`, `@OneToMany orderItems`. `OrderItem` (`quantity`, `priceAtPurchase`) `@ManyToOne` to `Order` and `Product`. Add both repos.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Repository` (slide 6); bean class requirements (slide 7); beans for database access (slide 25). `08-JPA.pdf` – `@Entity` for each class (slide 3); `@ManyToOne` for buyer → Order relation; `@OneToMany` for Order → OrderItems list; `mappedBy` to specify the owning side; JPA creates a join table for `@OneToMany` (slides 5, 6); avoid bidirectional loops (slide 6).

- [x] **5.2 - Checkout page.** `/checkout` shows cart summary + shipping address field.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@GetMapping` (slide 6); `Model` with cart data (slide 14); `07-SpringBeans.pdf` – `@Resource` injection of CartBean (session scope) (slide 21). `08-thymeleaf.pdf` – build checkout form with `th:action="@{/checkout}"`, `th:object`, and `th:field="*{shippingAddress}"` (slides 13, 14); iterate cart summary items with `th:each` (slide 10).

- [x] **5.3 - Place order (`@Transactional`).** `OrderService.placeOrder(...)` creates the Order + OrderItems atomically.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Service` for business logic (slide 6); constructor injection of repositories into the service (slides 9, 10); `07-LongPolling.pdf` – thread pool: the controller must **not block** the server thread; the service runs within the same thread but `@Transactional` ensures atomicity (slide 2 and slide 8 for the non-blocking / DeferredResult parallel concept). `08-JPA.pdf` – DB integrity problem: when multiple writes must succeed or fail together (slide 31); `@Transactional` ensures all operations are atomic; if any step throws an exception Spring rolls back all changes (slides 32, 33, 34); rule of thumb: any method with more than one write operation needs `@Transactional` (slide 33).

- [x] **5.4 - Post-order cleanup.** Clear the cart; mark purchased products `SOLD`.
  > 📖 **Materials:** `07-SpringBeans.pdf` – accessing the `@SessionScope` CartBean and clearing it (slides 17, 21); `@Service` method coordination (slide 6); `07-SpringMVC.pdf` – redirect after POST-order to avoid double-submission (slide 9). `08-JPA.pdf` – use `repository.save(product)` after setting `product.setStatus("SOLD")` to persist the update (slide 29); keep both updates inside the same `@Transactional` method for atomicity (slide 34).

- [x] **5.5 - My Orders page.** `/orders` lists the current (fake) user's orders with status.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@GetMapping` (slide 6); `Model.addAttribute(list)` (slide 14); Thymeleaf rendering (slide 13). `08-thymeleaf.pdf` – iterate orders with `th:each="order : ${orders}"` (slide 10); show status with `th:text="${order.status}"` (slide 3); use `th:switch="${order.status}"` / `th:case` for status-colored badges (slide 9). `08-JPA.pdf` – query method `findByBuyer(User buyer)` to get only the current user's orders (slides 10, 11).

- [x] **5.6 - RecommendationService.** Collect categories from the current user's past orders, then suggest other ACTIVE products in those categories via `findByCategoryInAndStatus(...)`, excluding already-purchased items. No new entity — pure service/query logic.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Service` for recommendation logic (slide 6); constructor injection of `OrderRepository`, `ProductRepository`, `CurrentUserProvider` (slides 9, 10). `08-JPA.pdf` – traverse `Order` → `OrderItem` → `Product` to collect categories; query method `findByCategoryInAndStatus(...)` (slides 10, 11); use `@Query` if method-name syntax is insufficient (slide 13).

- [x] **5.7 - Show recommendations.** "Recommended for you" section on Home/Browse driven by `RecommendationService`; "Related products" (same `category`) block on Product Detail.
  > 📖 **Materials:** `07-SpringMVC.pdf` – pass recommendation list via `Model.addAttribute()` (slide 14); `@GetMapping` on Home/Browse/Product controllers (slide 6). `08-thymeleaf.pdf` – iterate recommendations with `th:each="product : ${recommendations}"` (slide 10); product cards with `th:href="@{/product/{id}(id=${product.id})}"` (slide 7); conditionally hide section with `th:if="${!#lists.isEmpty(recommendations)}"` (slide 8). `08-JPA.pdf` – `findByCategoryAndStatus(...)` for related products on detail page (slides 10, 11).

---

## Phase 6 - Selling & product management

**Goal:** Let a user create/edit/delete products, including image upload and validation.

- [ ] **6.1 - Sell form (no image yet).** `/sell` form posts a new Product owned by the current user.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@PostMapping` (slide 6); `@RequestBody` or `@RequestParam` (slides 7, 8); redirect after successful POST (slide 9); `07-SpringBeans.pdf` – DTO bean for form data (slide 25); validation annotations on the DTO (slide 26). `08-thymeleaf.pdf` – build sell form: `th:action="@{/sell}"`, `th:object="${product}"`, `th:field="*{title}"`, `th:field="*{price}"` etc. (slides 13, 14). `08-JPA.pdf` – persist with `repository.save(product)` (slide 29).

- [ ] **6.2 - Server-side validation.** Add `@Valid` + `BindingResult`; show field errors in the form.
  > 📖 **Materials:** `07-SpringBeans.pdf` – validation with `@NotBlank`, `@Email`, `@Pattern`, `@Min`/`@Max` on the bean class (slide 26); `@Valid` on the controller parameter (slide 26); important remarks about validation even when client-side exists (slide 27); `07-SpringMVC.pdf` – `BindingResult` in controller method parameters (slide 17); Thymeleaf displaying validation errors (slide 31 — "More on thymeleaf"). `08-thymeleaf.pdf` – display per-field errors with `th:if="${#fields.hasErrors('price')}"` and `th:errors="*{price}"` (slide 15); display all errors as a list with `th:each="err : ${#fields.errors('product.*')}"` (slide 16); style error messages with a CSS class using `th:class="${#fields.hasErrors('price')}? error"` (slide 16). `08-JPA.pdf` – validation annotations on the Product entity: `@NotBlank`, `@Min`, `@DecimalMin` etc. (slides 14, 15); add `spring-boot-starter-validation` dependency to `pom.xml` (slide 16); use `BindingResult` immediately after `@Valid` in controller signature; if `result.hasErrors()` return the form view again (slides 19, 30).

- [ ] **6.3 - Image upload.** Accept `MultipartFile`, save to disk (or DB), serve it; show on cards/detail.
  > 📖 **Materials:** `07-SpringMVC.pdf` – upload files: `<input type="file">` on the form (slide 25); receive as `@RequestParam("myfile") MultipartFile file` (slide 25); save contents as `@Lob byte[]` in the entity (slides 25, 26); serve via `@GetMapping("/file/{id}")` returning `ResponseEntity<byte[]>` with `Content-Disposition` header (slide 26).

- [ ] **6.4 - My Products page.** `/my-products` shows the user's active/sold items.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@GetMapping` (slide 6); `Model.addAttribute(list)` (slide 14); Thymeleaf iteration (slide 13). `08-thymeleaf.pdf` – iterate with `th:each="product : ${myProducts}"` (slide 10); show status badge with `th:switch` / `th:case` (slide 9). `08-JPA.pdf` – use `findBySeller(User seller)` query method (slides 10, 11).

- [ ] **6.5 - Edit / delete product.** Edit form + delete action from My Products.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@PutMapping` / `@DeleteMapping` (slide 6); `@PathVariable` for product id (slide 8); `@RequestBody` or `@RequestParam` for update data (slides 7, 8); redirect after action (slide 9). `08-thymeleaf.pdf` – pre-populate the edit form with existing data using `th:field="*{title}"` inside `th:object="${product}"` (slides 13, 14); link to edit page with `th:href="@{/product/{id}/edit(id=${product.id})}"` (slide 7). `08-JPA.pdf` – update with `repository.save(product)` (existing entity with set id performs UPDATE); delete with `repository.deleteById(id)` (slide 9).

---

## Phase 7 - Admin backend & extra persistence

**Goal:** An admin area to manage everything, plus the remaining entities. Still guarded only by a temporary check.

- [ ] **7.1 - Admin dashboard.** `/admin` shows counts + tables of users, products, orders.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@GetMapping` (slide 6); passing multiple model attributes (slide 14); `ModelMap` for many attributes (slide 15); `07-SpringBeans.pdf` – `@Service` aggregates counts from multiple repos (slide 6). `08-thymeleaf.pdf` – display stats with `th:text="${productCount}"` (slide 3); render summary tables with `th:each` (slide 10). `08-JPA.pdf` – `repository.count()` returns the total number of rows; `findAll()` returns all entities (slide 9).

- [ ] **7.2 - Admin manage products.** Delete/flag any product.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@DeleteMapping` / `@PostMapping` with action param (slide 6); `@PathVariable` (slide 8); redirect after action (slide 9). `08-JPA.pdf` – `repository.deleteById(id)` or `repository.delete(entity)` (slide 9); for flag/status update use `repository.save(product)` after modifying field (slide 29).

- [ ] **7.3 - Admin manage users.** View/disable users.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@GetMapping` / `@PostMapping` (slide 6); `@PathVariable` (slide 8); `07-SpringBeans.pdf` – `@Service` for user management logic (slide 6). `08-JPA.pdf` – `findAll()` for listing all users; `save(user)` after setting `enabled=false`; use `findByUsername(String name)` query method (slides 9, 10, 11).

- [ ] **7.4 - SavedSearch entity.** `SavedSearch` (`keyword`, `category`, `minPrice`, `maxPrice`) `@ManyToOne User` + repo. Persist a user's searches (the 6th entity).
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Repository` (slide 6); bean requirements (slide 7); beans for database access (slide 25). `08-JPA.pdf` – `@Entity` with fields + `@ManyToOne User owner` foreign key (slides 3, 5); Roadmap 1/6: entity definition (slide 25); Roadmap 2/6: `JpaRepository` interface + `findByOwner(User owner)` query method (slides 26, 10).

- [ ] **7.5 - Activity log + Interceptor.** Log key actions (login, order, product changes) via a `HandlerInterceptor`; show in admin.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Component` for the interceptor bean (slide 6); `@Autowired` / constructor injection of the log repository (slides 8, 9); singleton scope — the interceptor is one instance shared by all requests, so thread-safety applies (slide 17); `07-LongPolling.pdf` – thread pool reminder: each HTTP request runs on its own thread, the interceptor runs in that thread context (slide 2).

- [ ] **7.6 - SupportMessage entity + repo.** `SupportMessage` (`subject`, `body`, `senderType` USER/SUPPORT, `status` OPEN/CLOSED, `createdAt`) with `@ManyToOne User`. Repo: `findByUser(User)`, `findByStatus(...)`. (8th entity.)
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Repository` (slide 6); bean requirements (slide 7); `@Converter` note for enum-to-DB mapping (slide 29). `08-JPA.pdf` – `@Entity` with `@ManyToOne User`; `@CreationTimestamp` on `createdAt` (slide 3); query methods `findByUser(...)` and `findByStatus(...)` (slides 10, 11); Roadmap 1/6 for entity structure (slide 25); Roadmap 2/6 for `JpaRepository` interface (slide 26).

- [ ] **7.7 - Contact support (user side).** `/support` page: form to submit a message + list of the user's own messages and replies.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@GetMapping` / `@PostMapping` (slide 6); `@Valid` form submission (slide 17); `Model.addAttribute(list)` (slide 14); redirect after POST (slide 9). `07-SpringBeans.pdf` – `@Service` for support message logic (slide 6); validation with `@NotBlank` on subject/body (slide 26). `08-thymeleaf.pdf` – support form with `th:action="@{/support}"`, `th:object`, `th:field="*{subject}"`, `th:field="*{body}"` (slides 13, 14); iterate messages with `th:each="msg : ${messages}"` (slide 10); show sender type with `th:switch` / `th:case` (slide 9). `08-JPA.pdf` – `save(supportMessage)` (slide 29).

- [ ] **7.8 - Admin support inbox.** `/admin/support`: list all message threads, reply as SUPPORT, mark resolved (CLOSED).
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@GetMapping` / `@PostMapping` (slide 6); `@PathVariable` for message id (slide 8); redirect after reply/resolve (slide 9); `Model.addAttribute(list)` (slide 14). `07-SpringBeans.pdf` – `@Service` coordinates reply + status update (slide 6). `08-thymeleaf.pdf` – iterate threads with `th:each` (slide 10); reply form with `th:action` and `th:field` (slides 13, 14); status badge with `th:switch` / `th:case` (slide 9). `08-JPA.pdf` – `save(message)` after setting `status=CLOSED` or appending reply (slide 29).

---

## Phase 8 - Robustness & polish (before turning on security)

**Goal:** Make it sturdy and friendly.

- [ ] **8.1 - Custom error pages.** `error/403.html`, `error/404.html`, `error/500.html`.
  > 📖 **Materials:** `07-SpringMVC.pdf` – define default error page: `server.error.path=/error` in `application.properties`; implement `ErrorController`; return the error view (slide 21). `08-thymeleaf.pdf` – error pages are standard Thymeleaf templates; add `xmlns:th` and use the layout fragment like any other view (slide 2); use `th:text="${message}"` to display the error description (slide 3).

- [ ] **8.2 - Global exception handling.** `@ControllerAdvice` for clean error feedback.
  > 📖 **Materials:** `07-SpringMVC.pdf` – define a default error page to catch uncaught exceptions (slide 21); `07-SpringBeans.pdf` – `@Component`-based bean managed by Spring (slide 6); important remarks: "define a global error page to catch unexpected errors such as validation errors" (slide 27). `08-JPA.pdf` – use `@ExceptionHandler(MethodArgumentNotValidException.class)` + `@ResponseStatus(HttpStatus.BAD_REQUEST)` to return structured error JSON with field names and messages (slide 18).

- [ ] **8.3 - Flash messages.** Success/error banners after actions (add to cart, order placed, product saved).
  > 📖 **Materials:** `07-SpringMVC.pdf` – redirect vs forward: redirect sends 302 to browser (client-side), use `RedirectAttributes` for flash scope (slide 9, slide 16); `07-SpringBeans.pdf` – `@RequestScope` bean could hold flash data per request (slide 17). `08-thymeleaf.pdf` – display the flash message banner conditionally with `th:if="${successMessage != null}"` and `th:text="${successMessage}"` in the layout fragment (slides 8, 3).

- [ ] **8.4 - Input validation pass.** Ensure every form has server-side `@Valid` checks.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@NotBlank`, `@Email`, `@Pattern` on bean/DTO fields (slide 26); `@Valid` + `BindingResult` in the controller (slide 26); why server-side validation is mandatory even with client-side validation (slide 26); important remarks about `@Valid` (slide 27); `07-SpringMVC.pdf` – `BindingResult` as a controller method parameter (slide 17); displaying errors with Thymeleaf (slide 31). `08-thymeleaf.pdf` – show per-field errors with `th:if="${#fields.hasErrors('field')}"` + `th:errors="*{field}"` (slide 15); show all errors as a list (slide 16); apply error CSS class dynamically with `th:class` (slide 16). `08-JPA.pdf` – full list of validation annotations: `@NotNull`, `@NotEmpty`, `@NotBlank`, `@Size`, `@Min`, `@Max`, `@Email`, `@Positive`, `@Past` etc. (slide 15); add `spring-boot-starter-validation` to `pom.xml` (slide 16); when to handle validation errors: always on server; client-side is UX only (slide 17); catch with `BindingResult` (slide 19); or use `@ExceptionHandler` for REST (slide 18).

---

## Phase 9 - Authentication & Authorization (LOGIN - DONE LAST)

**Goal:** Replace the "open everything" config and the fake current user with real Spring Security.

- [ ] **9.1 - Password hashing.** Add `BCryptPasswordEncoder` bean; hash seeded users' passwords.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Bean` inside a `@Configuration` class to explicitly declare the encoder (slides 12, 14); `@Autowired` / constructor injection wherever the encoder is used (slides 8, 9).

- [ ] **9.2 - UserDetailsService.** Load users from `UserRepository` for authentication.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Service` (slide 6); constructor injection of `UserRepository` (slides 9, 10); `@Autowired` (slide 8). `08-JPA.pdf` – use `findByUsername(String username)` query method on the `UserRepository` (slides 10, 11); Roadmap 2/6 for repository setup (slide 26).

- [ ] **9.3 - Real SecurityConfig.** Rewrite the Phase 0 placeholder: form login, logout, CSRF on (default).
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@Configuration` + `@Bean` for explicit security bean declaration (slides 12, 14); `07-SpringMVC.pdf` – `application.properties` can hold security-related settings (slide 4).

- [ ] **9.4 - Login & Registration pages.** Login form + a registration form (`@Valid`) that creates a USER.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `@PostMapping` (slide 6); `@RequestBody` / `@RequestParam` (slides 7, 8); redirect after successful registration (slide 9); `@GetMapping` for multiple URLs e.g. `{"/login","/logout"}` (slide 6); `07-SpringBeans.pdf` – DTO with `@NotBlank`, `@Email`, `@Pattern` for password complexity (slide 26); `@Valid` + `BindingResult` (slide 26). `08-thymeleaf.pdf` – build login and registration forms with `th:action`, `th:object`, `th:field` (slides 13, 14); show registration validation errors inline with `th:errors` (slide 15). `08-JPA.pdf` – validation annotations on the registration DTO: `@NotBlank`, `@Email`, `@Size` (slides 14, 15); Roadmap 6/6: `@Valid` + `BindingResult` pattern for form submission (slide 30).

- [ ] **9.5 - Swap the fake current user.** `CurrentUserProvider` now reads the authenticated principal everywhere (reviews, orders, products, wishlist, support messages).
  > 📖 **Materials:** `07-SpringMVC.pdf` – `Principal` as a controller method parameter (slide 17, "Other params"); `07-SpringBeans.pdf` – the `CurrentUserProvider` bean is `@Component` / `@Service` injected by constructor (slides 6, 9).

- [ ] **9.6 - Role-based access.** `/admin/`** -> ADMIN only (including `/admin/support`); `/sell`, `/orders`, `/my-products`, `/wishlist`, `/support`, checkout -> authenticated. Home/Browse/Detail stay public.
  > 📖 **Materials:** `07-SpringMVC.pdf` – catching multiple URL patterns in one mapping (slide 6: `@GetMapping(value = {"/login","/logout"})`); `07-SpringBeans.pdf` – `@Configuration` SecurityConfig with role rules (slide 12, 14).

- [ ] **9.7 - Ownership rules.** Users can only edit/delete **their own** products.
  > 📖 **Materials:** `07-SpringMVC.pdf` – `Principal` parameter to get current user in the controller (slide 17); `07-SpringBeans.pdf` – `@Service` enforces the ownership check (slide 6).

- [ ] **9.8 - Cart merge on login.** Merge the session cart into the user's DB cart after login.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@SessionScope` CartBean (slide 21); session review / one session per user (slide 19); Spring sessions (slide 23); `07-LongPolling.pdf` – thread pool context: the merge happens in a single request thread (slide 2).

- [ ] **9.9 - Startup admin init.** An `ApplicationListener`/runner creates the default admin **only if it doesn't exist** (works on an empty DB).
  > 📖 **Materials:** `07-SpringBeans.pdf` – Bean lifecycle: `@PostConstruct` / `CommandLineRunner` for initialization logic (slide 28); `@Component` bean (slide 6). `08-JPA.pdf` – `CommandLineRunner`: `run()` is called after context loads; inject repository, check `repository.count() == 0` before inserting (slide 35).

---

## Phase 10 - Submission prep

- [ ] **10.1 - README.md.** Functionality overview, build/run instructions (`mvnw clean package`, `mvnw spring-boot:run`), admin credentials, notes.
  > 📖 **Materials:** `07-SpringMVC.pdf` – Spring Boot project structure overview (slides 3, 5, 18, 24). `08-JPA.pdf` – include DB configuration details from `application.properties` (slide 28).

- [ ] **10.2 - SQL dump.** Export `ex4` (with sample data) via phpMyAdmin into the repo.
  > 📖 **Materials:** `07-SpringMVC.pdf` – session store type JDBC / `spring.session.jdbc.initialize-schema=always` as reference (slide 24).

- [ ] **10.3 - Empty-DB test.** Drop `ex4`, restart app, confirm it self-initializes and runs.
  > 📖 **Materials:** `07-SpringBeans.pdf` – `@PostConstruct` in DataSeeder: "inserts only if table is empty" pattern (slide 28); Bean lifecycle ensures init runs after injection (slide 28). `08-JPA.pdf` – `ddl-auto=create` recreates tables on each run; `ddl-auto=update` keeps existing data — use `create` for clean test, `update` for production (slide 28); `CommandLineRunner.run()` for seeding (slide 35).

- [ ] **10.4 - Demo recording.** Short continuous video (<=12 min), both members present; link in README.
  > 📖 **Materials:** *(No specific slide material — this is a submission step.)*

---

## Requirement coverage check

- **Spring Boot MVC + Thymeleaf:** Phases 0-8 (controllers + views, server-side logic).
- **>= 5 major pages:** Home, Browse/Search, Product Detail, Cart, Orders, Sell, My Products, Wishlist, Support, Admin.
- **Sessions:** RecentSearchBean (2.5), CartBean (3.1), browsing state (2.4).
- **Beans & DI:** Services injected via constructor (1.6 onward).
- **JPA + MySQL `ex4`, >=4 related repos:** User, Product, Order, OrderItem, Review, SavedSearch, WishlistItem, SupportMessage (8 entities).
- **Spring Security (auth + authz + registration):** Phase 9.
- **Robustness:** validation, transactions, error pages, access control (Phases 5, 6, 8, 9).
- **Optional extras:** file upload (6.3), interceptor/activity log (7.5), product recommendations (5.6–5.7), customer support messaging (7.6–7.8).

---

## Material Index


| Material File        | Key Topics Covered                                                                                                                                                                                                                                                                                                                       | Steps That Use It                                                                                                                                      |
| -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `07-SpringMVC.pdf`   | `@Controller`, `@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping`, `@RequestParam`, `@PathVariable`, `@RequestBody`, Model/ModelMap/ModelAndView, Thymeleaf, static files, error pages, file upload, redirect/forward, `application.properties`                                                                                 | 0.1–0.7, 1.4–1.7, 2.1–2.4, 2.6, 3.2–3.5, 4.5–4.8, 5.2–5.7, 6.1–6.5, 7.1–7.3, 7.7–7.8, 8.1–8.4, 9.3–9.7, 10.1–10.2                                      |
| `07-SpringBeans.pdf` | `@Component`/`@Service`/`@Repository`, `@Bean`/`@Configuration`, `@Autowired`/`@Resource`, constructor/setter/field injection, `@SessionScope`/`@ApplicationScope`/`@RequestScope`, `@PostConstruct`/`@PreDestroy`, Validation (`@Valid`, `@NotBlank`, `@Email`, `@Pattern`), Spring sessions                                            | 0.4–0.5, 1.1–1.3, 1.6, 2.5–2.6, 3.1, 4.1–4.2, 4.4, 4.7–4.8, 5.1, 5.3–5.6, 6.1–6.2, 7.4–7.8, 8.2–8.4, 9.1–9.2, 9.5, 9.8–9.9                             |
| `07-LongPolling.pdf` | Web-server thread pool, single controller instance, race conditions, non-blocking design (DeferredResult), WebSockets, SSE — **background architecture awareness**                                                                                                                                                                       | 0.5 (thread model), 2.5 (session + threads), 3.1 (session + threads), 5.3 (non-blocking service), 7.5 (interceptor threading), 9.8 (cart merge thread) |
| `08-thymeleaf.pdf`   | Template engine setup (pom.xml dep + `xmlns:th`), `th:text`, `th:href`/`@{...}`, `th:each`, `th:if`/`th:unless`, `th:switch`/`th:case`, `th:object`/`th:field`/`th:action` (forms), `th:insert`/`th:replace` (fragments), `#fields.hasErrors`/`th:errors` (validation display), variable expressions `${...}` / `*{...}`                 | 0.5–0.6, 1.4–1.5, 1.7, 2.3, 2.6, 3.2–3.5, 4.3, 4.5–4.8, 5.2, 5.5–5.7, 6.1–6.2, 6.4–6.5, 7.1, 7.7–7.8, 8.1, 8.3–8.4, 9.4                                |
| `08-JPA.pdf`         | `@Entity`/`@Id`/`@GeneratedValue`, `@ManyToOne`/`@OneToMany`/`@ManyToMany`, `JpaRepository`/`CrudRepository`, query method naming, `@Query`/`@Modifying`, validation annotations (`@NotNull`, `@NotBlank`, `@Email`, `@Min`/`@Max`), `@Valid`/`BindingResult`, `@Transactional`, `CommandLineRunner`, `application.properties` DB config | 0.2–0.3, 1.1–1.3, 1.4–1.6, 2.1–2.4, 4.1, 4.3–4.4, 4.6–4.8, 5.1, 5.3–5.7, 6.1–6.2, 6.4–6.5, 7.1–7.4, 7.6–7.8, 8.2, 8.4, 9.2, 9.4, 9.9, 10.1, 10.3       |


