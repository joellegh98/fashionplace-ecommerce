# Grading — spring-project-noa_haberer-joelle_gharo

**Project:** "FashionPlace" — eBay-style marketplace (browse/search listings, session cart, checkout with stock/orders, sell products, wishlist, reviews, support, recommendations, admin moderation + activity log)

## Final grade: 93 / 100

## Penalties applied

- Auth / access control (permitAll default + B3 capped to -2): **−4**
- Race conditions (RC7 register + review-on-deleted-product): **−4**
- REST conventions (REST1): **−1**



| Axis | Pts | Notes |
|------|-----|-------|
| Core / scope (C) | 0 | Full marketplace: buyer + seller flows, cart→checkout→orders with stock management, wishlist, reviews, support tickets, recommendations, admin moderation, activity logging. |
| Auth redirection (B) | −4 | **B1-lite (−2):** the chain ends `anyRequest().permitAll()` (inverted default). Sensitive routes are explicitly protected (admin, sell, orders, checkout, wishlist, support, product edit/delete/review; `/api/wishlist/**` authenticated *before* the `/api/**` permitAll), so no exploitable leak — but the default is a smell. **B3 (−2):** no already-authenticated redirect from `/login`. |
| Transactions (T) | 0 | `placeOrder` is one atomic transaction (locked reads → stock checks → decrements → order+items), 14 sites, no misuse. |
| Race conditions (RC) | −4 | **RC3 oversell — handled (0):** `findByIdForUpdate` (PESSIMISTIC_WRITE) + **sorted lock ordering** + `assertPurchasable` (stock/deleted/flagged/own-listing) inside one transaction. **RC5 cart — handled (0):** every `CartBean` method `synchronized`, with `drain`/`restore` for atomic cart→order handoff. **RC7 (−2):** register checks `existsByUsername/Email` + unique constraints, but the concurrent race loser isn't specifically translated. **Review-on-deleted-product (−2):** `submitReview` validates the rating (`@Valid`) but never checks `product.isDeleted()/FLAGGED` — the detail page redirects for deleted products, but a **direct POST `/product/{id}/review` still records a review on a deleted listing** (exactly the demo's question). |
| Validation (V) | 0 | 10 `@Valid` handlers, 60 constraints, form DTOs, `BindingResult` handled. *(Review integrity notes, not separately deducted: no `UNIQUE(user,product)` on reviews → duplicate reviews possible; no purchase verification.)* |
| REST (REST) | −1 | Two small REST controllers: `GET /api/products/suggest`, `GET /api/products/{id}/image` (ResponseEntity + content type + 404), `POST /api/wishlist/toggle` (authenticated). Proper `ResponseEntity`/status; **REST1 (−1):** `/toggle` is action-shaped rather than a resource. Proportional. |
| DI / bean hygiene (DI) | 0 | Constructor injection, session beans via config, no statics. |
| Robustness (ROB) | 0 | `GlobalExceptionHandler` (`@ControllerAdvice`) covering NoSuchElement/AccessDenied/IllegalArgument+State/validation/generic → error page; CSRF on; BCrypt; activity-log interceptor. |


## User focus areas (the six robustness axes)

### 1. Auth / access control — **solid, inverted default (−4)**
Sensitive routes explicitly protected and correctly ordered (`/api/wishlist/**` before `/api/**`); admin role-gated; CSRF on. The `anyRequest.permitAll` fallback is the one design smell; B3 remains.

### 2. Transactions — **atomic checkout (0)**
`placeOrder` performs all reads/writes in one transaction with pessimistic locks.

### 3. Race conditions — **checkout & cart handled; two residues (−4)**
The hard parts are done right — pessimistic-lock checkout with sorted lock ordering (deadlock avoidance) and a fully synchronized cart. Residues: the register concurrent-duplicate isn't translated, and the review endpoint doesn't re-check product availability on a direct POST.

### 4. Validation — **strong (0)**
Broad DTO + `@Valid` coverage; the review gaps are state-checks (scored under RC), not missing constraints.

### 5. REST conventions — **proportional (−1)**
AJAX-scale endpoints with correct `ResponseEntity`/status; only the action-shaped `/toggle` is docked.

### 6. DI / bean hygiene — **clean (0)**
