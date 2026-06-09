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

- [ ] **0.1 - Boot check.** Run `./mvnw clean package` then `./mvnw spring-boot:run`. Confirm it starts on `http://localhost:8080`. (Security will redirect to a login page for now - that's expected.)
- [ ] **0.2 - Start the database.** `docker compose up -d`. Confirm phpMyAdmin at `http://localhost:8081`.
- [ ] **0.3 - Align the DB name to `ex4` (required by the exercise).**
  - In `docker-compose.yml`: `MYSQL_DATABASE: ex4`.
  - In `src/main/resources/application.properties`: change URL to `jdbc:mariadb://localhost:3306/ex4` and set real username/password matching docker-compose.
- [ ] **0.4 - Temporarily open all pages.** Add a `SecurityConfig` that **permits all requests** (so we can build UI without logging in). This file gets rewritten in Phase 9. Leave a `// TODO: lock down in Phase 9` comment.
- [ ] **0.5 - First controller + page.** `HomeController` returns a `home` Thymeleaf view that just says "FashionPlace". Confirm it loads with no login.
- [ ] **0.6 - Base layout fragment.** Create `templates/fragments/layout.html` (header with logo + nav placeholder, footer). Home page uses it.
- [ ] **0.7 - Static styling.** Add `static/css/style.css` and link it in the layout. Make Home look clean.

---

## Phase 1 - First entity end-to-end (Listing)

**Goal:** Show real products from the database. One entity, one repo, one service, two pages.

- [ ] **1.1 - Listing entity (minimal).** Fields: `id`, `title`, `description`, `price`. JPA `@Entity`. Let Hibernate create the table (`ddl-auto=update`).
- [ ] **1.2 - ListingRepository.** `extends JpaRepository<Listing, Long>`.
- [ ] **1.3 - Seed data at startup.** A `DataSeeder` (`CommandLineRunner` bean) inserts ~6 listings **only if the table is empty** (so it works on an empty DB).
- [ ] **1.4 - Browse page.** `/browse` lists all listings as cards. (Controller talks to repo directly for now.)
- [ ] **1.5 - Product detail page.** `/listing/{id}` shows one listing's full info.
- [ ] **1.6 - Introduce ListingService.** Move data access out of the controller into `ListingService` (constructor injection). Controller calls the service. (Demonstrates Beans + DI.)
- [ ] **1.7 - Nav links.** Add Home / Browse links to the layout header.

---

## Phase 2 - Search, filter & session state

**Goal:** Make Browse useful and introduce the first session-scoped bean.

- [ ] **2.1 - Keyword search.** Add a search box on Browse; `/browse?q=ring` filters by title (repo `findByTitleContainingIgnoreCase`).
- [ ] **2.2 - Extend Listing fields.** Add `category`, `condition`, `status` (e.g. ACTIVE/SOLD), `imageUrl`. Update seed data.
- [ ] **2.3 - Filters.** Add category + min/max price + condition filters to Browse (service builds the query).
- [ ] **2.4 - Sorting.** Add sort by price / newest. Keep current sort in the URL.
- [ ] **2.5 - RecentSearchBean (`@SessionScope`).** Store last N search keywords in session.
- [ ] **2.6 - Show recent searches.** Render them as clickable "chips" on the Browse page. (Satisfies the **session** requirement.)

---

## Phase 3 - Shopping cart (session-based, still no login)

**Goal:** A working cart that lives in the session - no account needed yet.

- [ ] **3.1 - CartBean (`@SessionScope`).** Holds a map/list of `{listingId, quantity}`.
- [ ] **3.2 - Add to cart.** Button on Product Detail posts to `/cart/add`.
- [ ] **3.3 - Cart page.** `/cart` shows items, quantities, line totals, grand total.
- [ ] **3.4 - Update / remove.** Change quantity and remove items from the cart page.
- [ ] **3.5 - Cart badge.** Show item count in the header (reads CartBean).

---

## Phase 4 - User & relations (data only, still no auth)

**Goal:** Introduce the User entity and wire relationships. We fake "who is logged in" for now.

- [ ] **4.1 - User entity + repo.** Fields: `id`, `username`, `email`, `passwordHash`, `role`, `address`, `createdAt`. Seed an admin user + a couple of regular users.
- [ ] **4.2 - Temporary "current user" helper.** A tiny `CurrentUserProvider` bean that returns a seeded user. (Phase 9 swaps this for the real logged-in user.)
- [ ] **4.3 - Listing -> seller relation.** `Listing` gets `@ManyToOne User seller`. Update seed + show seller name on Product Detail.
- [ ] **4.4 - Review entity.** `Review` (`rating 1-5`, `comment`, `createdAt`) with `@ManyToOne` to `User` and `Listing`. Repo included.
- [ ] **4.5 - Show reviews.** Display a listing's reviews + average rating on Product Detail.
- [ ] **4.6 - Add review form.** Post a review (attributed to the temporary current user).

---

## Phase 5 - Orders & checkout

**Goal:** Turn a cart into a persisted order. Introduces transactions.

- [ ] **5.1 - Order + OrderItem entities.** `Order` (`totalPrice`, `status`, `createdAt`, `shippingAddress`) `@ManyToOne buyer`, `@OneToMany orderItems`. `OrderItem` (`quantity`, `priceAtPurchase`) `@ManyToOne` to `Order` and `Listing`. Add both repos.
- [ ] **5.2 - Checkout page.** `/checkout` shows cart summary + shipping address field.
- [ ] **5.3 - Place order (`@Transactional`).** `OrderService.placeOrder(...)` creates the Order + OrderItems atomically.
- [ ] **5.4 - Post-order cleanup.** Clear the cart; mark purchased listings `SOLD`.
- [ ] **5.5 - My Orders page.** `/orders` lists the current (fake) user's orders with status.

---

## Phase 6 - Selling & listing management

**Goal:** Let a user create/edit/delete listings, including image upload and validation.

- [ ] **6.1 - Sell form (no image yet).** `/sell` form posts a new Listing owned by the current user.
- [ ] **6.2 - Server-side validation.** Add `@Valid` + `BindingResult`; show field errors in the form.
- [ ] **6.3 - Image upload.** Accept `MultipartFile`, save to disk (or DB), serve it; show on cards/detail.
- [ ] **6.4 - My Listings page.** `/my-listings` shows the user's active/sold items.
- [ ] **6.5 - Edit / delete listing.** Edit form + delete action from My Listings.

---

## Phase 7 - Admin backend & extra persistence

**Goal:** An admin area to manage everything, plus the remaining entities. Still guarded only by a temporary check.

- [ ] **7.1 - Admin dashboard.** `/admin` shows counts + tables of users, listings, orders.
- [ ] **7.2 - Admin manage listings.** Delete/flag any listing.
- [ ] **7.3 - Admin manage users.** View/disable users.
- [ ] **7.4 - SavedSearch entity.** `SavedSearch` (`keyword`, `category`, `minPrice`, `maxPrice`) `@ManyToOne User` + repo. Persist a user's searches (the 6th entity).
- [ ] **7.5 - Activity log + Interceptor.** Log key actions (login, order, listing changes) via a `HandlerInterceptor`; show in admin.

---

## Phase 8 - Robustness & polish (before turning on security)

**Goal:** Make it sturdy and friendly.

- [ ] **8.1 - Custom error pages.** `error/403.html`, `error/404.html`, `error/500.html`.
- [ ] **8.2 - Global exception handling.** `@ControllerAdvice` for clean error feedback.
- [ ] **8.3 - Flash messages.** Success/error banners after actions (add to cart, order placed, listing saved).
- [ ] **8.4 - Input validation pass.** Ensure every form has server-side `@Valid` checks.

---

## Phase 9 - Authentication & Authorization (LOGIN - DONE LAST)

**Goal:** Replace the "open everything" config and the fake current user with real Spring Security.

- [ ] **9.1 - Password hashing.** Add `BCryptPasswordEncoder` bean; hash seeded users' passwords.
- [ ] **9.2 - UserDetailsService.** Load users from `UserRepository` for authentication.
- [ ] **9.3 - Real SecurityConfig.** Rewrite the Phase 0 placeholder: form login, logout, CSRF on (default).
- [ ] **9.4 - Login & Registration pages.** Login form + a registration form (`@Valid`) that creates a USER.
- [ ] **9.5 - Swap the fake current user.** `CurrentUserProvider` now reads the authenticated principal everywhere (reviews, orders, listings).
- [ ] **9.6 - Role-based access.** `/admin/**` -> ADMIN only; `/sell`, `/orders`, `/my-listings`, checkout -> authenticated. Home/Browse/Detail stay public.
- [ ] **9.7 - Ownership rules.** Users can only edit/delete **their own** listings.
- [ ] **9.8 - Cart merge on login.** Merge the session cart into the user's DB cart after login.
- [ ] **9.9 - Startup admin init.** An `ApplicationListener`/runner creates the default admin **only if it doesn't exist** (works on an empty DB).

---

## Phase 10 - Submission prep

- [ ] **10.1 - README.md.** Functionality overview, build/run instructions (`mvnw clean package`, `mvnw spring-boot:run`), admin credentials, notes.
- [ ] **10.2 - SQL dump.** Export `ex4` (with sample data) via phpMyAdmin into the repo.
- [ ] **10.3 - Empty-DB test.** Drop `ex4`, restart app, confirm it self-initializes and runs.
- [ ] **10.4 - Demo recording.** Short continuous video (<=12 min), both members present; link in README.

---

## Requirement coverage check

- **Spring Boot MVC + Thymeleaf:** Phases 0-8 (controllers + views, server-side logic).
- **>= 5 major pages:** Home, Browse/Search, Product Detail, Cart, Orders, Sell, My Listings, Admin.
- **Sessions:** RecentSearchBean (2.5), CartBean (3.1), browsing state (2.4).
- **Beans & DI:** Services injected via constructor (1.6 onward).
- **JPA + MySQL `ex4`, >=4 related repos:** User, Listing, Order, OrderItem, Review, SavedSearch (6 entities).
- **Spring Security (auth + authz + registration):** Phase 9.
- **Robustness:** validation, transactions, error pages, access control (Phases 5, 6, 8, 9).
- **Optional extras:** file upload (6.3), interceptor/activity log (7.5).
