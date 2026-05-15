package com.cassinocards.cassino_api.controller.game;

import com.cassinocards.cassino_api.dto.game.request.*;
import com.cassinocards.cassino_api.dto.game.response.GameStateDTO;
import com.cassinocards.cassino_api.model.game.GameHistory;
import com.cassinocards.cassino_api.service.game.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<UUID> createGame() {
        return ResponseEntity.ok(gameService.createGame());
    }

    @PostMapping("/{gameId}/start")
    public ResponseEntity<Void> startGame(
            @PathVariable UUID gameId,
            @RequestBody StartGameRequestDTO request) {
        gameService.startGame(gameId, request.userOne(), request.userTwo());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameId}/state")
    public ResponseEntity<GameStateDTO> getGameState(
            @PathVariable UUID gameId,
            @RequestBody GameStateRequestDTO request) {
        return ResponseEntity.ok(gameService.getGameState(gameId, request.user()));
    }

    @PostMapping("/{gameId}/place")
    public ResponseEntity<Void> place(
            @PathVariable UUID gameId,
            @RequestBody PlaceRequestDTO request) {
        gameService.place(gameId, request.user(), request.handCard());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/capture")
    public ResponseEntity<Void> capture(
            @PathVariable UUID gameId,
            @RequestBody CaptureRequestDTO request) {
        gameService.capture(gameId, request.user(), request.handCard(), request.tableCards());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/capture-build")
    public ResponseEntity<Void> captureBuild(
            @PathVariable UUID gameId,
            @RequestBody CaptureBuildRequestDTO request) {
        gameService.captureBuild(gameId, request.user(), request.handCard());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/build")
    public ResponseEntity<Void> build(
            @PathVariable UUID gameId,
            @RequestBody BuildRequestDTO request) {
        gameService.build(gameId, request.user(), request.handCard(), request.selectedTableCards());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/extend-build")
    public ResponseEntity<Void> extendBuild(
            @PathVariable UUID gameId,
            @RequestBody ExtendBuildRequestDTO request) {
        gameService.extendBuild(gameId, request.user(), request.handCard(), request.selectedTableCards(), request.opponentTopCard());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<GameHistory>> getGameHistory(@PathVariable String username) {
        return ResponseEntity.ok(gameService.getGameHistory(username));
    }
}
