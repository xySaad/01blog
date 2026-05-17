# 01blog — Project Documentation

## Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Database Schema](#database-schema)
4. [Architecture](#architecture)
5. [Custom Annotations](#custom-annotations)
6. [Infrastructure Abstractions](#infrastructure-abstractions)
7. [RBAC System](#rbac-system)
8. [API Reference](#api-reference)
9. [Configuration & Environment](#configuration--environment)

---

## Overview

01blog is a RESTful blog platform built with Spring Boot. It supports post creation, commenting, likes, follows, user reporting, and a full role-based moderation system. The backend exposes a versioned JSON API under `/api/v1/`.

---

## Technology Stack

### Runtime

| Technology                                           | Version              | Role                                                           |
| ---------------------------------------------------- | -------------------- | -------------------------------------------------------------- |
| Java                                                 | 21                   | Language                                                       |
| Spring Boot                                          | 4.0.1                | Application framework                                          |
| Spring MVC (`spring-boot-starter-webmvc`)            | —                    | HTTP layer, argument resolvers, CORS                           |
| Spring Data JPA (`spring-boot-starter-data-jpa`)     | —                    | ORM / repository abstraction                                   |
| Spring Security Crypto                               | —                    | BCrypt password hashing (no full Spring Security filter chain) |
| Spring Mail (`spring-boot-starter-mail`)             | —                    | Email verification via SMTP (Gmail)                            |
| Spring Validation (`spring-boot-starter-validation`) | —                    | Bean validation on request DTOs                                |
| Hibernate                                            | (via JPA starter)    | JPA provider; used for `@Formula`, joined-table inheritance    |
| PostgreSQL                                           | 42.6.0 (JDBC driver) | Primary database                                               |

### Libraries

| Library                                 | Version          | Role                                              |
| --------------------------------------- | ---------------- | ------------------------------------------------- |
| jjwt-api / jjwt-impl / jjwt-jackson     | 0.11.5           | JWT creation and parsing                          |
| Hutool (`hutool-all`)                   | 5.8.25           | Snowflake ID generation (`IdUtil.getSnowflake()`) |
| dotenv-java (`cdimascio`)               | 3.0.0            | `.env` file loading at startup                    |
| Cloudinary (`cloudinary-http5`)         | 2.0.0            | Media upload and retrieval                        |
| Jackson (`tools.jackson` + `fasterxml`) | 3.x / 2.21       | JSON serialization; custom `Long` serializer      |
| AspectJ                                 | (via Spring AOP) | `@Before` advice for permission checks            |

### Build & Deployment

| Tool                 | Role                                                                                         |
| -------------------- | -------------------------------------------------------------------------------------------- |
| Maven 3.9            | Build tool                                                                                   |
| Docker (multi-stage) | `maven:3.9-eclipse-temurin-21-alpine` for build, `eclipse-temurin:21-jre-alpine` for runtime |
| golang-migrate       | Database migration runner (SQL files in `backend/migrations/`)                               |

---

## Migrations ([golang-migrate](https://github.com/golang-migrate/migrate)):

installation:

```bash
go get -u -d github.com/golang-migrate/migrate/cmd/migrate
```

create migration:

```bash
migrate create -ext=.sql -dir=migrations <migration_name>
```

run migrations:

```bash
migrate -path migrations -database 'postgres://srm@localhost:5432/01blog?sslmode=disable' up
```

## Database Schema

Migrations live in `backend/migrations/` and are applied with [golang-migrate](https://github.com/golang-migrate/migrate).

```
accounts          — email/password credentials + email verification code
sessions          — one active JWT session per account
users             — public profile (login, name) linked 1-to-1 with accounts
posts             — blog posts; public/hidden/deleted flags
comments          — comments on posts; soft-deleted
post_likes        — composite PK (user_id, post_id)
follows           — composite PK (user_id, follower_id)
reports           — base report table (joined-table inheritance)
  post_reports    — report targeting a post
  comment_reports — report targeting a comment
  user_reports    — report targeting a user
roles             — named roles with a position (lower = higher authority)
permissions       — permission scopes (e.g. "v1:reports:read")
role_permissions  — M:N join between roles and permissions
account_roles     — M:N join between accounts and roles
```

**Key design decisions:**

- IDs are 64-bit Snowflake integers, not auto-increment sequences, for distributed-safe generation.
- Soft deletes (`deleted boolean`) are used on posts, comments, and users; hard deletes are never issued from application code for content.
- `accounts.verification_code IS NULL` means the account is verified.
- The `reports` table uses JPA `InheritanceType.JOINED` — `post_reports`, `comment_reports`, and `user_reports` each join back to `reports` by primary key.

---

## Architecture

```
src/main/java/com/z01/blog/
├── Main.java                    — Entry point; loads .env before Spring starts
├── annotation/                  — Custom annotation definitions
├── api/v1/                      — REST controllers
│   └── moderation/              — Moderation-only endpoints
├── config/                      — Spring configuration beans & startup listeners
├── exception/                   — AppError enum + AppException
├── infrastructure/              — Framework-level abstractions (resolvers, aspect, interfaces)
├── model/                       — JPA entities, repos, and DTOs
│   ├── Audit/                   — Auditable enum, Deleteable/Hideable interfaces
│   ├── Comment/                 — Comment hierarchy
│   ├── DTO/                     — Request/response records
│   ├── Post/                    — Post hierarchy
│   ├── RBAC/                    — Role, Permission, AccountRole entities
│   ├── Report/                  — Report hierarchy
│   └── User/                    — User hierarchy
├── resolver/                    — HandlerMethodArgumentResolver implementations
└── services/                    — Business logic (Auth, Audit, Moderation, Email, Cloudinary)
```

### Entity Hierarchies (JPA Inheritance)

Several domain objects use a base class + specialization pattern to share common fields while keeping separate database tables or additional query capabilities:

```
PostModel (@MappedSuperclass)
  └── PostEntity  — plain entity used for writes
  └── PostExtra   — includes @Formula for likesCount, commentsCount, and @ManyToOne owner

AbstractComment (@MappedSuperclass) extends BaseEntity
  └── CommentModel  — plain entity used for writes
  └── CommentExtra  — includes @ManyToOne owner (UserEntity)

UserModel (@MappedSuperclass)
  └── UserEntity  — standard entity
  └── UserExtra   — includes @Formula for followersCount, followingCount

ReportModel (@Entity, InheritanceType.JOINED)
  └── PostReport
  └── CommentReport
  └── UserReport
```

### Request Flow

```
HTTP Request
    │
    ▼
WebConfig (CORS + argument resolvers registered)
    │
    ├── AuthResolver          resolves @Auth.User / @Auth.Account → account/user ID from JWT cookie
    └── EntityAccessResolver  resolves @EntityAccess → fetches entity from DB, enforces ownership
    │
    ▼
Controller method
    │
    ├── SecurityAspect (@Before AOP) — checks @RequiresPermission before method body runs
    │
    ▼
Service / Repository
    │
    ▼
GlobalExceptionHandler — maps AppException → HTTP status + error name string
```

---

## Custom Annotations

### `@Auth.User`

**Target:** Method parameter  
**Package:** `com.z01.blog.annotation.Auth`

Injects the **user ID** (long) of the authenticated caller. Resolved by `AuthResolver`, which reads the `jwt` cookie, validates it via `AuthService`, and returns `UserEntity.accountId`. Throws `USER_NOT_FOUND` or `ACCOUNT_IS_BANNED` if the user profile does not exist or is banned/deleted.

```java
@GetMapping("/api/v1/posts")
List<PostExtra> getAll(@Auth.User long userId) { ... }
```

### `@Auth.Account`

**Target:** Method parameter  
**Package:** `com.z01.blog.annotation.Auth`

Injects the **account ID** (long) from the JWT. Unlike `@Auth.User`, this does **not** check for a user profile or banned status — only that the JWT is valid and a session exists. Used in flows like registration and verification where the user profile may not yet exist.

```java
@GetMapping("/api/v1/me")
MeResponse getUserOwnInfo(@Auth.Account long accountId) { ... }
```

**Difference between `@Auth.User` and `@Auth.Account`:**

| Annotation      | Validates JWT | Requires user profile | Checks banned/deleted |
| --------------- | ------------- | --------------------- | --------------------- |
| `@Auth.Account` | ✅            | ❌                    | ❌                    |
| `@Auth.User`    | ✅            | ✅                    | ✅                    |

---

### `@EntityAccess`

**Target:** Method parameter  
**Package:** `com.z01.blog.annotation`

Resolves a path variable to a JPA entity and enforces access control. Resolved by `EntityAccessResolver`.

```java
@EntityAccess(mode = Mode.Read)   // fetch entity; no ownership check
@EntityAccess(mode = Mode.Write)  // fetch entity; assert caller owns it
@EntityAccess(mode = Mode.Write, repo = SomeRepo.class)  // explicit repo override
```

**How it works:**

1. Reads the path variable matching the parameter name (e.g. `{post}` → parameter `PostModel post`).
2. Looks up the entity type in `EntityRegistry` to find the associated repository bean.
3. Calls the method on that repo annotated with `@AccessMethod` to fetch the entity.
4. If the entity implements `RestrictedEntity`, calls `ensureAccess(userId, mode)` on it.

```java
@DeleteMapping("/api/v1/posts/{post}")
void deleteById(@EntityAccess(mode = Mode.Write) PostModel post) {
    post.deleted = true;
    postRepo.save(post);
}
```

**Access rules by entity type:**

| Entity            | Read                                          | Write                                                      |
| ----------------- | --------------------------------------------- | ---------------------------------------------------------- |
| `PostModel`       | allowed if public or owner; blocked if hidden | owner only                                                 |
| `AbstractComment` | parent post must be readable                  | comment author only                                        |
| `RoleModel`       | —                                             | caller's highest role must be above target role's position |

---

### `@AccessMethod`

**Target:** Repository method  
**Package:** `com.z01.blog.annotation`

Marks a single repository method as the canonical fetch method for `EntityAccessResolver`. The registry scans repos at startup and stores the one method annotated with `@AccessMethod` per entity type. That method must accept one argument (the entity ID) and return the entity (or `Optional`).

```java
public interface PostRepo extends JpaRepository<PostModel, Long> {
    @AccessMethod
    @Query("SELECT p FROM PostExtra p WHERE p.id = :id AND p.deleted = false ...")
    PostExtra findByIdAndDeletedFalse(long id);
}
```

If no `@AccessMethod` is found on a repo, startup throws a `RuntimeException`.

---

### `@RequiresPermission`

**Target:** Controller method  
**Package:** `com.z01.blog.annotation`

Declares that the annotated endpoint requires the caller to hold a specific permission scope. Enforced by `SecurityAspect` (AOP `@Before` advice) via `PermissionValidator`, which queries the DB for the caller's roles and their associated permissions.

```java
@GetMapping
@RequiresPermission(scope = "v1:reports:read", description = "Read reports")
List<ReportModel> getAllReports() { ... }
```

**Attributes:**

| Attribute     | Required | Description                                                              |
| ------------- | -------- | ------------------------------------------------------------------------ |
| `scope`       | ✅       | Permission identifier (e.g. `"v1:roles:write"`)                          |
| `description` | ❌       | Human-readable description; synced to the `permissions` table at startup |

**Auto-sync at startup:** `PermissionAutoSync` scans all controller methods for `@RequiresPermission`, upserts the scopes into the `permissions` table, and rebuilds the `root` role to always contain every known permission.

Throws `PERMISSION_DENIED` (403) if the check fails.

---

## Infrastructure Abstractions

### `RestrictedEntity<U>`

Interface implemented by entities that have ownership-based access rules. `EntityAccessResolver` calls `ensureAccess(user, mode)` after fetching the entity.

```java
public interface RestrictedEntity<U> {
    void ensureAccess(U user, EntityAccess.Mode mode);
}
```

`BaseEntity` provides the default implementation (write = owner only). `PostModel` and `RoleModel` override it with additional rules.

---

### `PrincipalProvider<U>`

Interface for supplying the current caller's identity. The default implementation is `JwtPrincipalProvider`, which reads the `jwt` cookie and delegates to `AuthService`. Can be swapped for testing or alternative auth strategies.

---

### `PermissionProvider`

Interface with a single `hasPermission(String scope)` method. The implementation `PermissionValidator` queries `AccountRoleModel.repo` to check if the caller's account has a role that contains the requested scope.

---

### `EntityRegistry`

Spring `ApplicationListener` that builds a registry of `{EntityType → RepoMethod}` at startup by scanning all `@RestController` beans for `@EntityAccess` parameters, then locating the associated repository and the method annotated with `@AccessMethod`.

---

### `Deleteable` / `Hideable`

Audit action interfaces applied in `AuditService`:

- `Deleteable.delete()` — sets `deleted = true` (soft delete)
- `Hideable.hide()` — sets `hidden = true` (hides from feed but not from owner)

`AuditService.auditMaterial()` uses `instanceof` pattern matching to dispatch to the right interface, throwing `MATERIAL_NOT_DELETEABLE` / `MATERIAL_NOT_HIDEABLE` if the entity does not implement the expected interface.

---

## RBAC System

Roles are ordered by `position` (lower = higher authority, `0` = root, `Integer.MAX_VALUE` = default). Every account is assigned the `default` role on registration. The `root` role is assigned only to the bootstrap account.

**Permission check path:**

```
@RequiresPermission(scope)
  → SecurityAspect.doPermissionCheck()
    → PermissionValidator.hasPermission(scope)
      → AccountRoleModel.repo.existsById_AccountIdAndRole_Permissions_Scope(accountId, scope)
```

**Role access control** (`RoleModel.ensureAccess`): a user can only modify roles whose `position` is strictly greater than their own highest role position. The `root` (0) and `default` (`MAX_VALUE`) roles cannot be modified by anyone.

---

## API Reference

All endpoints are prefixed with `/api/v1/`. Authentication uses an `HttpOnly` cookie named `jwt`.

### Auth

| Method | Path        | Auth    | Description                        |
| ------ | ----------- | ------- | ---------------------------------- |
| POST   | `/register` | —       | Register; sends verification email |
| POST   | `/login`    | —       | Login; sets JWT cookie             |
| POST   | `/verify`   | Account | Submit verification code           |
| GET    | `/me`       | Account | Get own profile + permissions      |

### Users

| Method | Path                    | Auth              | Description                  |
| ------ | ----------------------- | ----------------- | ---------------------------- |
| POST   | `/users`                | Account           | Create user profile          |
| GET    | `/users/{id}`           | User              | Get user profile             |
| DELETE | `/users`                | Account           | Soft-delete own account      |
| GET    | `/users/{id}/posts`     | User              | Get user's posts             |
| GET    | `/users/search/{query}` | User + permission | Search users by login prefix |

### Posts

| Method | Path            | Auth         | Description                        |
| ------ | --------------- | ------------ | ---------------------------------- |
| POST   | `/posts/`       | User         | Create post                        |
| POST   | `/posts/{post}` | User (owner) | Update post                        |
| GET    | `/posts`        | User         | Feed (public, not own, not hidden) |
| GET    | `/posts/{post}` | User         | Get single post                    |
| DELETE | `/posts/{post}` | User (owner) | Soft-delete post                   |

### Comments

| Method | Path                     | Auth         | Description           |
| ------ | ------------------------ | ------------ | --------------------- |
| GET    | `/posts/{post}/comments` | —            | List comments on post |
| POST   | `/posts/{post}/comments` | User         | Create comment        |
| PUT    | `/comments/{comment}`    | User (owner) | Update comment        |
| DELETE | `/comments/{comment}`    | User (owner) | Soft-delete comment   |

### Likes

| Method | Path                  | Auth | Description    |
| ------ | --------------------- | ---- | -------------- |
| GET    | `/posts/{post}/likes` | User | Check if liked |
| POST   | `/posts/{post}/likes` | User | Like post      |
| DELETE | `/posts/{post}/likes` | User | Unlike post    |

### Follows

| Method | Path               | Auth | Description   |
| ------ | ------------------ | ---- | ------------- |
| POST   | `/follow/{userId}` | User | Follow user   |
| DELETE | `/follow/{userId}` | User | Unfollow user |

### Reports

| Method | Path      | Auth | Description                         |
| ------ | --------- | ---- | ----------------------------------- |
| GET    | `/report` | —    | List valid report reasons           |
| POST   | `/report` | User | Submit a report (POST/COMMENT/USER) |

### Media

| Method | Path                  | Auth         | Description               |
| ------ | --------------------- | ------------ | ------------------------- |
| POST   | `/posts/{post}/media` | User (owner) | Upload file to Cloudinary |
| GET    | `/posts/{post}/media` | User (owner) | List post's media URLs    |

### Moderation (requires permissions)

| Method | Path                                   | Permission         | Description                |
| ------ | -------------------------------------- | ------------------ | -------------------------- |
| GET    | `/moderation/reports`                  | `v1:reports:read`  | List all reports           |
| GET    | `/moderation/reports/{id}/post`        | `v1:reports:read`  | Reported post              |
| GET    | `/moderation/reports/{id}/comment`     | `v1:reports:read`  | Reported comment           |
| GET    | `/moderation/reports/{id}/user`        | `v1:reports:read`  | Reported user              |
| POST   | `/moderation/audit/report`             | `v1:reports:audit` | Resolve report with action |
| POST   | `/moderation/audit/report/{id}/ignore` | `v1:reports:audit` | Ignore report              |
| POST   | `/moderation/audit/content`            | `v1:content:audit` | Act on content directly    |
| GET    | `/moderation/roles`                    | `v1:roles:read`    | List all roles             |
| POST   | `/moderation/roles`                    | `v1:roles:write`   | Create/update role         |
| DELETE | `/moderation/roles/{role}`             | `v1:roles:write`   | Delete role                |
| GET    | `/moderation/roles/{id}/users`         | `v1:roles:read`    | List users in role         |
| POST   | `/moderation/roles/{role}/users`       | `v1:roles:write`   | Add/remove users from role |

---

## Configuration & Environment

**`application.yaml`** — base config, references env vars via `${VAR}`:

```yaml
jwt:
  secret: ${JWT_SECRET} # HMAC key; must be ≥256 bits
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/01blog
    username: srm
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

**Required environment variables:**

| Variable         | Description                                                                  |
| ---------------- | ---------------------------------------------------------------------------- |
| `JWT_SECRET`     | Secret key for signing JWTs                                                  |
| `MAIL_USERNAME`  | Gmail address for sending verification emails                                |
| `MAIL_PASSWORD`  | Gmail app password                                                           |
| `CLOUDINARY_URL` | Cloudinary connection URL                                                    |
| `ROOT_PASSWORD`  | Password for the bootstrap root account (can be set interactively if absent) |

Variables can be provided via a `.env` file in the working directory (loaded by `dotenv-java` before Spring starts) or as standard environment variables.

### Long Serialization

The custom `JacksonConfig` registers a `Long` serializer that checks for the `X-JSON-Format: long-as-string` request header. When present, all `long`/`Long` values are serialized as JSON strings — useful for JavaScript clients where 64-bit Snowflake IDs exceed `Number.MAX_SAFE_INTEGER`.
