package com.cassinocards.cassino_api.game.card;

import com.cassinocards.cassino_api.game.card.attributes.Color;
import com.cassinocards.cassino_api.game.card.attributes.Rank;
import com.cassinocards.cassino_api.game.card.attributes.Suit;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Card {

    private Suit suit;
    private Color color;
    private Rank rank;
    private int points;

    public Card(Suit suit, Color color, Rank rank) {
        this.suit = suit;
        this.color = color;
        this.rank = rank;
        this.points = calculatePoints();
    }

    public int getValue() {
        return rank.getValue();
    }

    private int calculatePoints() {
        if (rank.getValue() == 1) {
            return 1;
        }

        if (rank.getValue() == 2 && suit == Suit.SPADES) {
            return 1;
        }

        if (rank.getValue() == 10 && suit == Suit.DIAMOND) {
            return 2;
        }

        return 0;
    }

    @Override
    public String toString() {
        return rank.name() + "_OF_" + suit.name();
    }

    public String toShortString() {
        return this.rank.getSymbol() + this.suit.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;
        return suit == card.suit && color == card.color && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, color, rank);
    }
}
