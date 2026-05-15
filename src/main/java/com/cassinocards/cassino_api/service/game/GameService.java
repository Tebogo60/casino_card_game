package com.cassinocards.cassino_api.service.game;

import com.cassinocards.cassino_api.dto.game.response.GameStateDTO;
import com.cassinocards.cassino_api.repository.game.GameHistoryRepository;
import com.cassinocards.cassino_api.game.Game;
import com.cassinocards.cassino_api.game.card.Card;
import com.cassinocards.cassino_api.game.state.GameState;
import com.cassinocards.cassino_api.model.game.GameHistory;
import com.cassinocards.cassino_api.model.user.User;
import com.cassinocards.cassino_api.shared.exception.GameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GameService {

    private final Map<UUID, Game> games = new ConcurrentHashMap<>();
    private final GameHistoryRepository gameHistoryRepository;

    private Game getGame(UUID gameId) {
        Game game = games.get(gameId);
        if (game == null) throw new GameException("Game not found: " + gameId);
        return game;
    }

    public UUID createGame() {
        UUID gameId = UUID.randomUUID();
        games.put(gameId, new Game(gameId));
        return gameId;
    }

    public void startGame(UUID gameId, User userOne, User userTwo) {
        Game game = getGame(gameId);
        game.startGame(userOne, userTwo);
        //the history saved as is even without winner
        createGameHistory(game);
    }

    public void place(UUID gameId, User user, Card handCard) {
        Game game = getGame(gameId);
        game.place(user, handCard);
        completeGameHistory(game);
    }

    public void capture(UUID gameId, User user, Card handCard, List<Card> tableCards) {
        Game game = getGame(gameId);
        game.capture(user, handCard, tableCards);
        completeGameHistory(game);
    }

    public void captureBuild(UUID gameId, User user, Card handCard) {
        Game game = getGame(gameId);
        game.captureBuild(user, handCard);
        completeGameHistory(game);
    }

    public void build(UUID gameId, User user, Card handCard, List<Card> selectedTableCards) {
        Game game = getGame(gameId);
        game.build(user, handCard, selectedTableCards);
        completeGameHistory(game);
    }

    public void extendBuild(UUID gameId, User user, Card handCard, List<Card> selectedTableCards, Card opponentTopCard) {
        Game game = getGame(gameId);
        game.extendBuild(user, handCard, selectedTableCards, opponentTopCard);
        completeGameHistory(game);
    }

    public GameStateDTO getGameState(UUID gameId, User user) {
        return GameStateDTO.from(getGame(gameId), user);
    }

    public List<GameHistory> getGameHistory(String username) {
        return gameHistoryRepository.findByPlayerOneUsernameOrPlayerTwoUsername(username, username);
    }

    private void createGameHistory(Game game) {
        GameHistory history = GameHistory.builder()
                .gameId(game.getGameId())
                .playerOneUsername(game.getPlayerOne().getUser().getUsername())
                .playerTwoUsername(game.getPlayerTwo().getUser().getUsername())
                .build();
        gameHistoryRepository.save(history);
    }

    private void completeGameHistory(Game game) {
        if (game.getGameState() != GameState.SCORE) return;
        GameHistory history = gameHistoryRepository.findByGameId(game.getGameId())
                .orElseThrow(() -> new GameException("Game history not found"));
        history.setPlayerOneScore(game.getPlayerOne().getFinalScore());
        history.setPlayerTwoScore(game.getPlayerTwo().getFinalScore());
        history.setWinnerUsername(game.getWinner().getUser().getUsername());
        gameHistoryRepository.save(history);
        games.remove(game.getGameId());
    }
}