# Cassino API

A RESTful backend for **Cassino** Card Game. 

Cassino Cards is an online adaptation of the most beloved card game from South African townships. Long before the internet, this game was played on stoeps, in backyards, and on street corners wherever two people had a deck of cards and time to think. Played with only 40 cards, it has been passed down through generations across all ages, not because it is simple, but because it rewards the sharp and punishes the careless.

This is not a game of luck. You can hold the best hand and still lose to someone who has nothing but a plan. Every turn whether you place, capture, or build, reveals something about how you think. Cassino tests memory, patience, and the ability to read your opponent while hiding your own intentions. This API powers that experience online, bringing the corner to anyone with a browser.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Game Rules Implemented](#game-rules-implemented)
- [Architecture](#architecture)
- [API Endpoints](#api-endpoints)
- [Data Models](#data-models)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [Roadmap](#roadmap)

---

## Overview

Cassino API handles all server-side logic for a two-player Cassino card game. The server owns the full game state in-memory during an active session and persists a history record to a relational database when the game completes.

Key responsibilities:
- Game creation and player initialization
- Turn validation and enforcement
- All legal game actions: place, capture, build, extend build, capture build
- End-of-hand detection and automatic re-dealing
- Scoring calculation and winner resolution
- Persistent game history for completed games

---

## Tech Stack

| Layer | Technology                          |
|---|-------------------------------------|
| Language | Java 21                             |
| Framework | Spring Boot 3                       |
| Persistence | Spring Data JPA / Hibernate         |
| Database | MySQL (or any JPA-compatible RDBMS) |
| Boilerplate reduction | Lombok                              |
| Build tool | Maven                               |

---

## Project Structure

```
src/main/java/com/cassinocards/cassino_api/
.
├── CassinoApiApplication.java
├── controller
│   ├── game
│   │   └── GameController.java
│   └── user
│       ├── UnverifiedUserController.java
│       └── UserController.java
├── dto
│   ├── game
│   │   ├── request
│   │   │   ├── BuildRequestDTO.java
│   │   │   ├── CaptureBuildRequestDTO.java
│   │   │   ├── CaptureRequestDTO.java
│   │   │   ├── ExtendBuildRequestDTO.java
│   │   │   ├── GameStateRequestDTO.java
│   │   │   ├── PlaceRequestDTO.java
│   │   │   └── StartGameRequestDTO.java
│   │   └── response
│   │       ├── BuildDTO.java
│   │       ├── CardDTO.java
│   │       └── GameStateDTO.java
│   └── user
│       ├── request
│       │   ├── CreateUnverifiedUserDTO.java
│       │   ├── CreateUserDTO.java
│       │   ├── ForgotPasswordDTO.java
│       │   ├── LoginRequestDTO.java
│       │   └── ResetPasswordDTO.java
│       └── response
│           ├── AuthResponseDTO.java
│           ├── GetUnverifiedUserDTO.java
│           └── LoginResponseDTO.java
├── game
│   ├── card
│   │   ├── attributes
│   │   │   ├── Color.java
│   │   │   ├── Rank.java
│   │   │   └── Suit.java
│   │   ├── Card.java
│   │   ├── Deck.java
│   │   └── Hand.java
│   ├── Game.java
│   ├── player
│   │   └── Player.java
│   ├── rules
│   │   ├── Build.java
│   │   └── Table.java
│   └── state
│       ├── GameState.java
│       └── PlayerState.java
├── model
│   ├── game
│   │   └── GameHistory.java
│   └── user
│       ├── AuthProvider.java
│       ├── PasswordResetToken.java
│       ├── UnverifiedUser.java
│       ├── User.java
│       └── UserRole.java
├── repository
│   ├── game
│   │   └── GameHistoryRepository.java
│   └── user
│       ├── PasswordResetTokenRepository.java
│       ├── UnverifiedUserRepository.java
│       └── UserRepository.java
├── service
│   ├── game
│   │   └── GameService.java
│   └── user
│       ├── JwtService.java
│       ├── UnverifiedUserService.java
│       ├── UserDetailsServiceImpl.java
│       └── UserService.java
└── shared
    ├── config
    │   ├── CorsConfig.java
    │   ├── JwtAuthFilter.java
    │   └── SecurityConfig.java
    ├── EmailService.java
    └── exception
        ├── CardNotFoundException.java
        ├── EmailAlreadyExistsException.java
        ├── GameException.java
        ├── GlobalExceptionHandler.java
        ├── IllegalActionException.java
        ├── InvalidTokenException.java
        ├── UserFoundException.java
        └── UserNotFoundException.java
```

---

## Game Rules Implemented

### Actions per turn
| Action | Description |
|---|---|
| `place` | Place a card face-up on the table |
| `capture` | Capture table cards whose values sum to the played card's value |
| `captureBuild` | Capture an existing build using the matching card |
| `build` | Combine a hand card with table cards to create a build |
| `extendBuild` | Extend an existing build by adding more cards |

### Scoring
Points are awarded based on a player's collection at the end of the game:

| Condition | Points |
|---|---|
| Card point values (face cards, aces, etc.) | Variable |
| Exactly 5 spades | +1 |
| More than 5 spades | +2 |
| Exactly 20 cards collected | +1 |
| More than 20 cards collected | +2 |
| Last capturer (remaining table cards) | All leftover table cards added to collection |

### Hand structure
- Each player is dealt **10 cards** per hand
- A full game consists of **2 hands** (40 cards total from a standard deck)
- After the first hand, cards are redealt automatically
- After the second hand, scoring is calculated and a winner is determined

---

## Architecture

### Domain Layer (`game/`)
The core game logic has **no Spring dependencies**. `Game` and `Table` are plain Java objects managed in-memory by `GameService`.

- **`Game`** — orchestrates turn flow, delegates all rule enforcement to `Table`
- **`Table`** — owns validation and mutation of table state (cards, builds). Validates before mutating — no partial state on failure
- **`Player`** — holds hand, collection, score, and turn state
- **`Deck`** — standard 40-card deck with shuffle and draw

### Service Layer (`GameService`)
- Maintains a `ConcurrentHashMap<UUID, Game>` for active games
- Delegates all game logic to `Game`
- Persists a `GameHistory` record on game start, updates it on game completion
- Evicts completed games from the map after persisting

### State Machine
```
WAITING → FIRST_HAND → SECOND_HAND → SCORE
```

---

## API Endpoints

Base URL: `/api/v1/game`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/create` | Create a new game, returns `UUID` |
| `POST` | `/{gameId}/start` | Start the game with two players |
| `GET` | `/{gameId}/state` | Get current game state for requesting user |
| `POST` | `/{gameId}/place` | Place a card on the table |
| `POST` | `/{gameId}/capture` | Capture table cards |
| `POST` | `/{gameId}/capture-build` | Capture an existing build |
| `POST` | `/{gameId}/build` | Create a build |
| `POST` | `/{gameId}/extend-build` | Extend an existing build |
| `GET` | `/history/{username}` | Get game history for a user |

### Example — Game State Response
```json
{
  "gameId": "550e8400-e29b-41d4-a716-446655440000",
  "gameState": "FIRST_HAND",
  "currentPlayerUsername": "alice",
  "myHand": [
    { "suit": "SPADES", "rank": "ACE", "value": 1 }
  ],
  "tableCards": [
    { "suit": "HEARTS", "rank": "FIVE", "value": 5 }
  ],
  "playerOneBuild": null,
  "playerTwoBuild": null
}
```

---

## Data Models

### `GameHistory`
Persisted once when a game starts, updated when it ends.

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGINT` | Auto-generated PK |
| `game_id` | `UUID` | Unique, maps to in-memory game |
| `player_one_username` | `VARCHAR` | Set on start |
| `player_two_username` | `VARCHAR` | Set on start |
| `player_one_score` | `INT` | Set on completion |
| `player_two_score` | `INT` | Set on completion |
| `winner_username` | `VARCHAR` | Set on completion |
| `created_at` | `TIMESTAMP` | Auto |
| `updated_at` | `TIMESTAMP` | Auto |

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- MySQL (or preferred JPA-compatible DB)

### Installation

```bash
# Clone the repository
git clone https://github.com/Tebogo60/cassino_card_game.git
cd cassino_card_game

# Configure your database (see Environment Variables)

# Build and run
./mvnw spring-boot:run
```

---

## Environment Variables

Configure in `application.properties` or as environment variables:

```properties
spring.datasource.url=jdbc:mysql://localhost:5432/cassino
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
```

---

## Roadmap

- [ ] JWT authentication — resolve `User` from token instead of request body
- [ ] WebSocket support — real-time game state push to clients
- [ ] Expanded game history — move-by-move replay
- [ ] Unit and integration tests
- [ ] Docker support

---

> Backend only. Frontend (React) is maintained in a separate repository.