package com.cassinocards.cassino_api.game.card;

import com.cassinocards.cassino_api.game.card.attributes.Color;
import com.cassinocards.cassino_api.game.card.attributes.Rank;
import com.cassinocards.cassino_api.game.card.attributes.Suit;
import lombok.Getter;

import java.util.*;

@Getter
public class Deck {

    public static final int DESK_SIZE = 40;
    public final Deque<Card> cards;

    public Deck() {
        this.cards = new ArrayDeque<>(generate());
    }

    // generate a card
    private List<Card> generate() {

        List<Card> deck = new ArrayList<>(DESK_SIZE);

        Map<Color, List<Suit>> colorSuit = new HashMap<>();
        colorSuit.put(Color.RED, new ArrayList<>(List.of(Suit.HEART, Suit.DIAMOND)));
        colorSuit.put(Color.BLACK, new ArrayList<>(List.of(Suit.CLUBS, Suit.SPADES)));

        List<Rank> ranks = new ArrayList<>(List.of(Rank.values()));

        colorSuit.forEach((color, suitList) -> {
            for (Suit suit : suitList) {
                for (Rank rank : ranks) {
                    deck.add(new Card(suit, color, rank));
                }
            }
        });

        Collections.shuffle(deck);
        return deck;
    }

    public int remaining() {
        return cards.size();
    }
}
