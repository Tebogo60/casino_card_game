# 🃏 Cassino Cards – System & Code Design Document

## 1. Project Overview

### Purpose
Build a turn-based multiplayer online Cassino card game where players compete to capture cards and score points.

### Tech Stack
- Frontend: React  
- Backend: Spring Boot  
- Database: MySQL (recommended)  
- Realtime: WebSockets (STOMP or native WebSocket)

### Game Modes
- PvP (2–4 players, MVP = 2 players)
- PvE (Player vs AI – future phase)

---

## 2. High-Level Architecture

```

[ React Frontend ]
|
| REST + WebSocket
↓
[ Spring Boot Backend ]
|        |        |
Game    Match   User/Auth
Engine   Service  Service
|
[ Database (MySQL) ]

````

---

## 3. Core Backend Modules

### 3.1 User Service
Handles authentication and player data.

**User Entity**
```java
User {
  id: UUID
  username: String
  email: String
  password: String
}
````

---

### 3.2 Game Service

Responsible for:

* Game state management
* Turn handling
* Rule enforcement
* Scoring

---

### 3.3 Match Service

* Matchmaking (Play Now)
* Room creation (Create Game)
* AI opponent setup

---

## 4. Game Domain Model

### Card Model

```java
Card {
  suit: enum { HEARTS, DIAMONDS, CLUBS, SPADES }
  value: int // 1–10 only
}
```

---

### Player Model

```java
Player {
  id: UUID
  hand: List<Card>
  capturedCards: List<Card>
  score: int
  isTurn: boolean
}
```

---

### Game State

```java
Game {
  id: UUID
  players: List<Player>
  tableCards: List<Card>
  activeBuild: Build
  deck: Stack<Card>
  currentTurnPlayerId: UUID
  status: WAITING | IN_PROGRESS | FINISHED
}
```

---

### Build System

```java
Build {
  value: int
  ownerPlayerId: UUID
  sourceCards: List<Card>
  isActive: boolean
}
```

---

## 5. Game Rules Engine

### Turn Flow

Each turn allows ONE action:

```
TURN → ACTION → VALIDATION → UPDATE STATE → SWITCH TURN
```

Actions:

* TAKE (Capture)
* BUILD
* PLACE (Trail)

---

### Capture Logic

#### Direct Match

```
if playedCard.value == tableCard.value → capture
```

#### Sum Match

```
if playedCard.value == sum(selectedTableCards) → capture
```

---

### Build Logic

#### Create Build

```
min requirement:
- 2 table cards
- 1 hand card

buildValue = sum(tableCards) + handCard
```

#### Extend Build

```
newBuildValue = existingBuild + selectedTableCards + handCard
```

Rules:

* Only 1 active build
* Any player can capture build
* Build persists until resolved

---

### Place (Trail)

```
Add card → tableCards
End turn
```

---

## 6. Scoring System

| Condition           | Points |
| ------------------- | ------ |
| 10 of Diamonds      | 2      |
| Each Ace            | 1      |
| 2 of Spades         | 1      |
| 20 cards collected  | 1      |
| 21+ cards collected | 2      |
| 5 spades            | 1      |
| 6+ spades           | 2      |

---

## 7. Game End Logic

```
Game ends when:
- Both players have no cards left
```

Final rule:

* Remaining table cards go to last player who captured

---

## 8. API Design

### Auth

```
POST /auth/register
POST /auth/login
```

### Game

```
POST /game/create
POST /game/join/{gameId}
GET  /game/state/{gameId}
POST /game/action
```

---

### Action Payload

```json
{
  "gameId": "uuid",
  "playerId": "uuid",
  "action": "TAKE | BUILD | PLACE",
  "selectedCards": ["cardIds"],
  "playedCard": "cardId"
}
```

---

## 9. WebSocket Events

Channel:

```
/game/{gameId}
```

Events:

* GAME_START
* TURN_UPDATE
* ACTION_RESULT
* GAME_STATE_SYNC
* GAME_END

---

## 10. Frontend Structure

### Pages

* Landing Page
* Lobby
* Game Room
* Rules Page

---

### Components

```
Card
Table
PlayerHand
GameControls
ScoreBoard
```

---

### State Management

* Redux Toolkit OR Zustand (MVP-friendly)

---

## 11. AI Player (Future)

Basic strategy:

* Prefer capture moves
* Else build
* Else place lowest card

---

## 12. MVP Scope

### Must Have

* 2-player gameplay
* Turn system
* Capture logic
* Build system
* Scoring

### Nice to Have

* AI mode
* Reconnect system
* Spectator mode

---

## 13. Build Order

1. Domain models
2. Game engine logic
3. REST API
4. WebSocket sync
5. Frontend UI
6. Multiplayer sync
7. Scoring system

