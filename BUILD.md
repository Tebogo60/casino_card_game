# Cassino Cards – Spring Boot Backend Design Plan

> Base package: `com.cassinocards.cassino_api`

---

## Table of Contents

- [1. Project Structure](#1-project-structure)
- [2. Layer Responsibilities](#2-layer-responsibilities)
  - [2.1 game/ — Pure Java Game Logic](#21-game----pure-java-game-logic)
  - [2.2 model/ — JPA Entities](#22-model----jpa-entities)
  - [2.3 repository/ — Spring Data JPA](#23-repository----spring-data-jpa)
  - [2.4 service/ — Business Logic](#24-service----business-logic)
  - [2.5 controller/ — REST + WebSocket Endpoints](#25-controller----rest--websocket-endpoints)
  - [2.6 dto/ — Data Transfer Objects](#26-dto----data-transfer-objects)
- [3. Real-Time Communication (WebSocket)](#3-real-time-communication-websocket)
- [4. Security](#4-security)
- [5. Technology Decisions](#5-technology-decisions)
- [6. Data Flow for a Player Move](#6-data-flow-for-a-player-move)
- [7. Build Order](#7-build-order)

---

## 1. Project Structure

```
com.cassinocards.cassino_api
├── controller/
│   ├── user/          # auth & user endpoints
│   └── game/          # game lifecycle + websocket
├── service/
│   ├── user/          # auth, JWT, user management
│   └── game/          # game actions, matchmaking, scoring
├── model/
│   └── user/          # JPA entities only (no DTOs here)
├── repository/
│   ├── user/
│   └── game/
├── dto/
│   ├── user/          # migrate existing user DTOs here
│   └── game/          # game request/response shapes
├── game/              # pure Java game logic — no Spring
│   ├── card/          # Card, Deck, Hand
│   ├── state/         # GameState, PlayerState, Table, Build
│   └── rules/         # CaptureValidator, BuildValidator, ScoringEngine
├── websocket/         # STOMP message types & wrappers
└── shared/
    ├── config/        # Security, WebSocket, CORS
    ├── exception/     # Custom exceptions + global handler
    └── EmailService.java
```

> **Key mental model:** `game/` is pure game logic (no DB, no Spring). `model/` is what lives in the database. `service/` is the bridge between them. `shared/` owns anything that crosses feature boundaries.

---

## 2. Layer Responsibilities

### 2.1 `game/` — Pure Java Game Logic

This is where the game *thinks*. Nothing here touches the DB or HTTP. Organised into three sub-packages:

**`game/card/`**
- `Card.java` — suit, rank, value
- `Deck.java` — builds and shuffles the 40-card deck
- `Hand.java` — a player's held cards

**`game/state/`**
- `GameState.java` — full snapshot: table, players, whose turn it is
- `PlayerState.java` — hand + collection pile per player
- `Table.java` — cards + active build currently on the table
- `Build.java` — build value, underlying cards, owner ID

**`game/rules/`**
- `CaptureValidator.java` — validates direct match & sum match
- `BuildValidator.java` — validates build & extension logic
- `ScoringEngine.java` — calculates points after game ends

> **Note:** Keep all classes in `game/` as plain POJOs — no `@Entity`, no `@Service`, no Spring annotations. They are easy to unit test in isolation, which matters because the rules are complex.

---

### 2.2 `model/` — JPA Entities

JPA entities only — no DTOs nested here. DTOs live in `dto/` at the top level.

```
model/
├── user/
│   ├── User.java                  # auth: username, password hash, stats
│   ├── UnverifiedUser.java        # pending email verification
│   ├── PasswordResetToken.java    # password reset flow
│   ├── UserRole.java              # role enum
│   └── AuthProvider.java          # local / OAuth provider
└── game/
    ├── GameEntity.java            # gameId, status (WAITING/IN_PROGRESS/FINISHED), mode
    ├── GamePlayerEntity.java      # join table: which users are in which game
    └── GameSnapshotEntity.java    # serialized GameState JSON stored per turn
```

> **Note on `GameSnapshotEntity`:** Instead of mapping every card to a DB column, serialize the entire `GameState` as a JSON string. This keeps the schema simple while the `game/` layer handles all the structure.

---

### 2.3 `repository/` — Spring Data JPA

One interface per entity, extending `JpaRepository`. Spring Data generates the queries.

```java
// Example — this is all you need for basic queries
public interface GameRepository extends JpaRepository<GameEntity, UUID> {
    List<GameEntity> findByStatus(GameStatus status);
}
```

---

### 2.4 `service/` — Business Logic

```
service/
├── user/
│   ├── AuthService.java               # registration, login, JWT token issuance
│   ├── UserService.java               # user management
│   ├── UnverifiedUserService.java     # email verification flow
│   ├── JwtService.java                # JWT generation & validation
│   └── UserDetailsServiceImpl.java    # Spring Security bridge
└── game/
    ├── GameService.java               # create/join/find game, persist state
    ├── GameActionService.java         # process TAKE / BUILD / PLACE moves
    ├── MatchmakingService.java        # Play Now: find or create an open game
    └── ScoringService.java            # delegates to ScoringEngine, saves results
```

`GameActionService` is your most critical class. Its flow for every move:

1. Load `GameSnapshotEntity` from DB → deserialize to `GameState`
2. Validate it's actually this player's turn
3. Delegate to `CaptureValidator` or `BuildValidator`
4. Mutate the `GameState`
5. Check for end-of-game condition
6. Re-serialize `GameState` → save back to DB
7. Broadcast updated state via WebSocket

---

### 2.5 `controller/` — REST + WebSocket Endpoints

```
controller/
├── user/
│   ├── AuthController.java             # POST /api/auth/register, /api/auth/login
│   ├── UserController.java             # user profile & management endpoints
│   └── UnverifiedUserController.java   # email verification endpoints
└── game/
    ├── GameController.java             # POST /api/games, GET /api/games/{id}
    └── GameWebSocketController.java    # @MessageMapping — handles in-game moves
```

> **Note:** Split REST (lobby/auth) from WebSocket (in-game actions). REST is for setup; WebSocket is for the live game.

---

### 2.6 `dto/` — Data Transfer Objects

Never expose your entities or domain objects directly. DTOs are the contract with the frontend.

```
dto/
├── user/                              # all existing user DTOs migrated here
│   ├── AuthResponseDTO.java
│   ├── LoginRequestDTO.java
│   ├── CreateUserDTO.java
│   ├── CreateUnverifiedUserDTO.java
│   ├── GetUnverifiedUserDTO.java
│   ├── ForgotPasswordDTO.java
│   └── ResetPasswordDTO.java
└── game/
    ├── request/
    │   └── GameActionRequest.java     # { actionType, cardPlayed, targetCards[] }
    └── response/
        ├── GameStateResponse.java     # sanitized view of GameState
        └── GameSummaryResponse.java   # final scores after game ends
```

> **Important:** `GameStateResponse` should only expose what a player is allowed to see — their own hand in full, but the opponent's hand only as a count.

---

## 3. Real-Time Communication (WebSocket)

Use **STOMP over WebSocket** — Spring Boot has built-in support via `spring-boot-starter-websocket`.

| Direction | Destination |
|---|---|
| Client subscribes (receives) | `/topic/game/{gameId}` |
| Client sends (moves) | `/app/game/{gameId}/action` |

Every time a player acts, the server pushes the new `GameState` (as a DTO) to `/topic/game/{gameId}`. Both players' frontends update simultaneously.

**WebSocket message types (`websocket/` package):**

- `IncomingAction.java` — type: `TAKE | BUILD | PLACE`, payload: card selections
- `OutgoingEvent.java` — type: `STATE_UPDATE | GAME_OVER | ERROR`, payload: `GameStateResponse`

---

## 4. Security

Uses **Spring Security + JWT** (already implemented for auth).

- `AuthController` issues a JWT on login
- `JwtAuthFilter` validates the token on every request (lives in `shared/config/`)
- WebSocket connections also pass the JWT in the STOMP CONNECT headers
- `SecurityConfig` and `CorsConfig` live in `shared/config/`

---

## 5. Technology Decisions

| Concern | Tool | Why |
|---|---|---|
| REST API | Spring MVC (`@RestController`) | Standard, well-documented |
| Real-time | STOMP WebSocket | Built into Spring, works well with React |
| Database | PostgreSQL + JPA/Hibernate | Relational for users/games, JSON column for game state |
| Auth | Spring Security + JWT | Stateless, fits multiplayer well (already built) |
| Game state storage | JSON serialization (Jackson) | Flexible, avoids complex schema |
| Testing | JUnit 5 + Mockito | Test `game/` rules independently of Spring |

---

## 6. Data Flow for a Player Move

```
React Frontend
    │
    │  WS: { actionType: "TAKE", cardPlayed: "7♠", targetCards: ["7♥"] }
    ▼
GameWebSocketController
    │  extract playerId from JWT principal
    ▼
GameActionService
    ├── load GameSnapshotEntity from DB
    ├── deserialize → GameState  (game/state/)
    ├── validate turn ownership
    ├── call CaptureValidator.validate()  (game/rules/)
    ├── apply capture → mutate GameState
    ├── check isGameOver()
    ├── serialize GameState → save snapshot
    └── if game over → call ScoringService
    ▼
WebSocket Broker
    │  broadcast GameStateResponse to /topic/game/{gameId}
    ▼
Both React clients update UI
```

---

## 7. Build Order

| Step | What to Build | Notes |
|---|---|---|
| 1 | **`game/` layer** | `Card`, `Deck`, `GameState`, validators, scoring. Write unit tests first. |
| 2 | **DB schema** | Entities + repositories for User, Game, GameSnapshot |
| 3 | **Auth** | Already built — register/login with JWT |
| 4 | **Game lifecycle** | Create game, join game, REST endpoints |
| 5 | **WebSocket setup** | Configure STOMP, wire up `GameWebSocketController` |
| 6 | **Action processing** | `GameActionService` connecting `game/` layer → DB → WebSocket |
| 7 | **Matchmaking** | "Play Now" finding open games |
| 8 | **AI opponent** | A service that calls `GameActionService` the same way a human WebSocket message would |

> **AI note:** Because all moves go through `GameActionService`, an AI player is just a service that calls the same method a human WebSocket message would trigger — clean separation by design.