package com.cassinocards.cassino_api.game.card;

import com.cassinocards.cassino_api.game.card.attributes.Color;
import com.cassinocards.cassino_api.game.card.attributes.Rank;
import com.cassinocards.cassino_api.game.card.attributes.Suit;

import java.util.Objects;

public class Card {

    private Suit suit;
    private Color color;
    private Rank rank;

    public Card(Suit suit, Color color, Rank rank) {
        this.suit = suit;
        this.color = color;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Color getColor() {
        return color;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank.name() + "_OF_" + suit.name();
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
