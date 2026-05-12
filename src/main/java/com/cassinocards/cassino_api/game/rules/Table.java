package com.cassinocards.cassino_api.game.rules;

import com.cassinocards.cassino_api.game.card.Card;
import com.cassinocards.cassino_api.game.player.Player;
import com.cassinocards.cassino_api.shared.exception.IllegalActionException;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Table {

    private final List<Card> tableCards;
    private Build playerOneBuild;
    private Build playerTwoBuild;

    private boolean firstActionTaken;
    private boolean isFirstHand;

    public Table(boolean isFirstHand) {
        this.tableCards = new ArrayList<>();
        this.playerOneBuild = null;
        this.playerTwoBuild = null;
        this.firstActionTaken = false;
        this.isFirstHand = isFirstHand;
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(tableCards);
    }

    public Optional<Build> getBuildForPlayer(Player player) {
        if (playerOneBuild != null && playerOneBuild.isOwnedBy(player)) {
            return Optional.of(playerOneBuild);
        }
        if (playerTwoBuild != null && playerTwoBuild.isOwnedBy(player)) {
            return Optional.of(playerTwoBuild);
        }
        return Optional.empty();
    }

    public boolean hasBuildForPlayer(Player player) {
        return getBuildForPlayer(player).isPresent();
    }

    public boolean isEmpty() {
        return tableCards.isEmpty() && playerOneBuild == null && playerTwoBuild == null;
    }

    public void updateIsFirstHand(boolean firstHand) {
        isFirstHand = firstHand;
    }

    public void place(Player player, Card handCard) {
        if (!firstActionTaken) {
            tableCards.add(handCard);
            firstActionTaken = true;
            return;
        }

        if (hasBuildForPlayer(player) && isFirstHand) {
            throw new IllegalActionException("Cannot place while you own an active build");
        }

        tableCards.add(handCard);
    }

    public void captureBuild(Player player, Card captureCard, Build build) {
        if (captureCard.getValue() != build.getTargetValue()) {
            throw new IllegalActionException("Cannot capture this build, correct capture value: " + build.getTargetValue());
        }

        player.getHand().take(captureCard);
        List<Card> captured = new ArrayList<>(build.getCards());
        captured.add(captureCard);
        player.addToCollection(captured);

        if (build == playerOneBuild) {
            playerOneBuild = null;
        } else {
            playerTwoBuild = null;
        }
    }

    //capture table cards
    public void captureCards(Player player, Card captureCard, List<Card> tableCards) {
        if (!isValidCapture(captureCard, tableCards)) {
            throw new IllegalActionException("Cannot capture these cards, capture card value must be the sum of the cards");
        }

        player.getHand().take(captureCard);
        List<Card> allCaptured = new ArrayList<>(tableCards);
        allCaptured.add(captureCard);
        player.addToCollection(allCaptured);

        this.tableCards.removeAll(tableCards);
    }

    public boolean isValidCapture(Card captureCard, List<Card> cards) {
        return cards.stream()
                .mapToInt(Card::getValue)
                .sum() == captureCard.getValue();
    }

    public void build(Player player, Card handCard, List<Card> selectedTableCards) {
        if (!new HashSet<>(tableCards).containsAll(selectedTableCards)) {
            throw new IllegalActionException("Selected cards are not on the table");
        }

        if (hasBuildForPlayer(player)) {
            throw new IllegalActionException("Player already has an active build");
        }

        Build newBuild;
        if (selectedTableCards.size() == 1 && selectedTableCards.getFirst().getValue() == handCard.getValue()) {
            newBuild = new Build(player, handCard, selectedTableCards.getFirst());
        } else {
            newBuild = new Build(player, handCard, selectedTableCards);
        }

        int targetValue = newBuild.getTargetValue();
        if ((playerOneBuild != null && playerOneBuild.getTargetValue() == targetValue) ||
                (playerTwoBuild != null && playerTwoBuild.getTargetValue() == targetValue)) {
            throw new IllegalActionException("A build with target value " + targetValue + " already exists");
        }

        if (playerOneBuild == null) {
            playerOneBuild = newBuild;
        } else {
            playerTwoBuild = newBuild;
        }

        player.getHand().take(handCard);
        tableCards.removeAll(selectedTableCards);
    }

    public void extendBuild(Player player, Card handCard, List<Card> selectedTableCards, Card opponentTopCard) {
        Build build = getBuildForPlayer(player)
                .orElseThrow(() -> new IllegalActionException("No active build found for player"));

        if (!selectedTableCards.isEmpty() && opponentTopCard != null) {
            build.extend(player, handCard, selectedTableCards, opponentTopCard);
        } else if (!selectedTableCards.isEmpty()) {
            build.extend(player, handCard, selectedTableCards);
        } else if (opponentTopCard != null) {
            build.extend(player, handCard, opponentTopCard);
        } else {
            throw new IllegalActionException("Extension requires at least one table card or opponent top card");
        }

        tableCards.removeAll(selectedTableCards);
    }
}
