package com.cassinocards.cassino_api.game;

import com.cassinocards.cassino_api.game.card.Card;
import com.cassinocards.cassino_api.game.card.Deck;
import com.cassinocards.cassino_api.game.card.Hand;
import com.cassinocards.cassino_api.game.card.attributes.Suit;
import com.cassinocards.cassino_api.game.player.Player;
import com.cassinocards.cassino_api.game.rules.Build;
import com.cassinocards.cassino_api.game.rules.Table;
import com.cassinocards.cassino_api.game.state.GameState;
import com.cassinocards.cassino_api.game.state.PlayerState;
import com.cassinocards.cassino_api.model.user.User;
import com.cassinocards.cassino_api.shared.exception.GameException;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Game {

    private final UUID gameId;
    private final Deck deck;
    private final Table table;

    private Player playerOne;
    private Player playerTwo;
    private GameState gameState;
    private Player currentPlayer;
    private Player opponentPlayer;
    private Player winner;

    public Game(UUID gameId) {
        this.gameId = gameId;
        this.deck = new Deck();
        this.table = new Table(true);
        this.gameState = GameState.WAITING;
    }

    //initialize game
    //start lobby
    public void startGame(User userOne, User userTwo) {
        playerOne = new Player(userOne);
        playerTwo = new Player(userTwo);
        startFirstHand();
    }

    public void startFirstHand() {
        deck.shuffle();
        dealToPlayers();
        gameState = GameState.FIRST_HAND;
        currentPlayer = playerOne;
        opponentPlayer = playerTwo;
        currentPlayer.setState(PlayerState.TURN);
        playerTwo.setState(PlayerState.WAITING);
    }

    //deal 10 cards each player
    private void dealToPlayers() {
        for (int i = 0; i < Hand.HAND_SIZE; i++) {
            playerOne.getHand().addCard(deck.draw());
            playerTwo.getHand().addCard(deck.draw());
        }
    }

    //take turns
    private Player validateTurn(User user) {
        Player player = findPlayer(user);
        if (!currentPlayer.equals(player)) {
            throw new GameException("");
        }
        return player;
    }

    //find current player by user
    private Player findPlayer(User user) {
        if (playerOne.getUser().equals(user)) return playerOne;
        if (playerTwo.getUser().equals(user)) return playerTwo;
        throw new GameException("User is not a player in this game");
    }

    //switch turns
    private void changeTurns() {
        if (currentPlayer.equals(playerOne)) {
            currentPlayer = playerTwo;
            opponentPlayer = playerOne;
            opponentPlayer.setState(PlayerState.WAITING);
        } else {
            currentPlayer = playerOne;
            opponentPlayer = playerTwo;
            opponentPlayer.setState(PlayerState.WAITING);
        }

        updateGameState();
        currentPlayer.setState(PlayerState.TURN);
    }

    //
    private void updateGameState() {
        if (currentPlayer.getHand().isEmpty()
                && opponentPlayer.getHand().isEmpty()
                && deck.remaining() == Hand.HAND_SIZE * 2) {
            table.setFirstHand(false);
            dealToPlayers();
            gameState = GameState.SECOND_HAND;
        } else if (currentPlayer.getHand().isEmpty()
                && opponentPlayer.getHand().isEmpty()
                && deck.getCards().isEmpty()) {
            collectRemainingTableCards();
            gameState = GameState.SCORE;
            updateWinner();
        }
    }

    private void collectRemainingTableCards() {
        if (table.isEmpty()) return;

        Player lastCapturer;
        if (playerOne.isLastCapturer()) {
            lastCapturer = playerOne;
        } else {
            lastCapturer = playerTwo;
        }

        lastCapturer.addToCollection(table.getCards());
        table.clearCards();
    }

    private void updateWinner() {
        updateScore();
        if (playerOne.getFinalScore() > playerTwo.getFinalScore()) {
            winner = playerOne;
            return;
        }
        winner = playerTwo;
    }

    public int calculate(Player player) {
        List<Card> collection = player.getCollection();
        int sum = collection.stream()
                .mapToInt(Card::getPoints)
                .sum();

        if (sum > 0) {
            int spades = 0;
            for (Card card: collection) {
                if (card.getSuit() == Suit.SPADES) {
                    spades++;
                }
            }
            if (spades == 5) {
                sum++;
            } else if (spades > 5) {
                sum+=2;
            }
        }

        if (collection.size() == 20) {
            sum++;
        } else if (collection.size() > 20) {
            sum+=2;
        }

        return sum;
    }

    //updating final score fr each player
    private void updateScore() {
        playerOne.setFinalScore(calculate(playerOne));
        playerTwo.setFinalScore(calculate(playerTwo));
    }

    //action after capturing cards
    private void afterCapture() {
        currentPlayer.setLastCapturer(true);
        opponentPlayer.setLastCapturer(false);
        changeTurns();
    }

    //place card on the table
    public void place(User user, Card handCard) {
        Player player = validateTurn(user);
        table.place(player, player.getHand().take(handCard));
        changeTurns();
    }

    //capture cards on the table
    public void capture(User user, Card handCard, List<Card> tableCards) {
        Player player = validateTurn(user);
        table.captureCards(player, handCard, tableCards);
        afterCapture();
    }

    //get build by card
    private Build getBuild(Card card) {
        Build p1Build = table.getPlayerOneBuild();
        Build p2Build = table.getPlayerTwoBuild();
        if (p1Build != null && p1Build.getTargetValue() == card.getValue()) {
            return p1Build;
        }
        if (p2Build != null && p2Build.getTargetValue() == card.getValue()) {
            return p2Build;
        }
        throw new GameException("No matching build for card value: " + card.getValue());
    }

    //capture built cards
    public void captureBuild(User user, Card handCard) {
        Player player = validateTurn(user);
        table.captureBuild(player, handCard, getBuild(handCard));
        afterCapture();
    }

    //build a card
    public void build(User user, Card handCard, List<Card> selectedTableCards) {
        Player player = validateTurn(user);
        table.build(player, handCard, selectedTableCards);
        changeTurns();
    }

    //extend a build
    public void extendBuild(User user, Card handCard, List<Card> selectedTableCards, Card opponentTopCard) {
        Player player = validateTurn(user);
        table.extendBuild(player, handCard, selectedTableCards, opponentTopCard);
        changeTurns();
    }
}

