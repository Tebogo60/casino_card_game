package com.cassinocards.cassino_api.game.card;

import com.cassinocards.cassino_api.shared.exception.CardNotFoundException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Hand {

    public static final int HAND_SIZE = 10;
    public final List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public Card take(Card targetCard) {
        int cardIndex = cards.indexOf(targetCard);

        if (cardIndex == -1)
            throw new CardNotFoundException("Card Not Found");

        return cards.remove(cardIndex);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    @Override
    public String toString() {
        return cards.stream()
                .map(Card::toShortString)
                .toList()
                .toString();
    }
}
