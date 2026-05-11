package com.cassinocards.cassino_api.game.rules;

import com.cassinocards.cassino_api.game.card.Card;
import com.cassinocards.cassino_api.game.player.Player;
import com.cassinocards.cassino_api.shared.exception.IllegalActionException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class Build {

    private final Player owner;
    private final List<Card> cards;
    private final int targetValue;

    // build for more than 2 cards
    public Build(Player owner, Card handCard, List<Card> tableCards) {
        if (tableCards.isEmpty()) {
            throw new IllegalActionException("A build requires at least one table card");
        }

        this.cards = new ArrayList<>(tableCards);
        this.cards.add(handCard);
        this.cards.sort(Comparator.comparing(Card::getValue).reversed());

        int buildSum = this.cards.stream()
                .mapToInt(Card::getValue)
                .sum();

        if (buildSum < 2 || buildSum > 10) {
            throw new IllegalActionException("Build sum must be between 2 and 10, got: " + buildSum);
        }

        this.targetValue = buildSum;
        this.owner = owner;
    }

    // build when there's only 2 cards involved
    public Build(Player owner, Card handCard, Card tableCard) {
        if (handCard.getValue() != tableCard.getValue()) {
            throw new IllegalActionException(
                    "Top build requires matching values, got: "
                            + handCard.getValue() + " and " + tableCard.getValue());
        }

        this.cards = new ArrayList<>();
        this.cards.add(handCard);
        this.cards.add(tableCard);

        this.targetValue = handCard.getValue();
        this.owner = owner;
    }

    // extend: table cards + opponent top card
    public void extend(Player player, Card handCard, List<Card> tableCards, Card opponentTopCard) {
        if (!isOwnedBy(player)) {
            throw new IllegalActionException("Player can only extend their own builds");
        }

        List<Card> extendCards = new ArrayList<>(tableCards);
        extendCards.add(opponentTopCard);
        extendCards.add(handCard);

        validateExtend(extendCards, player, handCard);
    }

    // extend: table cards only
    public void extend(Player player, Card handCard, List<Card> tableCards) {
        if (!isOwnedBy(player)) {
            throw new IllegalActionException("Player can only extend their own builds");
        }
        if (tableCards.isEmpty()) {
            throw new IllegalActionException("Extension requires at least one table card");
        }

        List<Card> extendCards = new ArrayList<>(tableCards);
        extendCards.add(handCard);

        validateExtend(extendCards, player, handCard);
    }

    // extend: opponent top card only
    public void extend(Player player, Card handCard, Card opponentTopCard) {
        if (!isOwnedBy(player)) {
            throw new IllegalActionException("Player can only extend their own builds");
        }
        if (opponentTopCard == null) {
            throw new IllegalActionException("Opponent top card must not be null");
        }

        List<Card> extendCards = new ArrayList<>();
        extendCards.add(opponentTopCard);
        extendCards.add(handCard);

        validateExtend(extendCards, player, handCard);
    }

    private void validateExtend(List<Card> extendCards, Player player, Card handCard) {
        int cardsSum = extendCards.stream()
                .mapToInt(Card::getValue)
                .sum();

        if (cardsSum != targetValue) {
            throw new IllegalActionException(
                    "Extension cards must sum to target value: " + targetValue + ", got: " + cardsSum);
        }

        boolean stillHoldsMatchingCard = player.getHand().getCards().stream()
                .filter(c -> !c.equals(handCard))
                .anyMatch(c -> c.getValue() == targetValue);

        if (!stillHoldsMatchingCard) {
            throw new IllegalActionException(
                    "Owner must still hold a card matching build value after extending: " + targetValue);
        }

        sortCards(extendCards);
        this.cards.addAll(extendCards);
    }

    private void sortCards(List<Card> cards) {
        cards.sort(Comparator.comparing(Card::getValue).reversed());
    }

    public boolean isOwnedBy(Player player) {
        return this.owner.equals(player);
    }
}