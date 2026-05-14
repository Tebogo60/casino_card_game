package com.cassinocards.cassino_api.dto.game;

import com.cassinocards.cassino_api.game.Game;
import com.cassinocards.cassino_api.game.player.Player;
import com.cassinocards.cassino_api.game.rules.Build;
import com.cassinocards.cassino_api.model.user.User;

import java.util.List;
import java.util.UUID;

public record GameStateDTO(
        UUID gameId,
        String gameState,
        String currentPlayerUsername,
        List<CardDTO> myHand,
        List<CardDTO> tableCards,
        BuildDTO playerOneBuild,
        BuildDTO playerTwoBuild
) {
    public static GameStateDTO from(Game game, User requestingUser) {
        Player requestingPlayer = game.getPlayerOne().getUser().equals(requestingUser)
                ? game.getPlayerOne()
                : game.getPlayerTwo();

        Build p1Build = game.getTable().getPlayerOneBuild();
        Build p2Build = game.getTable().getPlayerTwoBuild();

        return new GameStateDTO(
                game.getGameId(),
                game.getGameState().name(),
                game.getCurrentPlayer().getUser().getUsername(),
                requestingPlayer.getHand().getCards().stream().map(CardDTO::from).toList(),
                game.getTable().getCards().stream().map(CardDTO::from).toList(),
                p1Build != null ? BuildDTO.from(p1Build) : null,
                p2Build != null ? BuildDTO.from(p2Build) : null
        );
    }
}