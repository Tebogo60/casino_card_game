package com.cassinocards.cassino_api.game.card;

import com.cassinocards.cassino_api.game.card.attributes.Color;
import com.cassinocards.cassino_api.game.card.attributes.Rank;
import com.cassinocards.cassino_api.game.card.attributes.Suit;
import lombok.Getter;

import java.util.*;

@Getter
public class Deck {

    private static final int DECK_SIZE = 40;
    private final Deque<Card> cards;
    private boolean deckShuffled = false;

    public Deck() {
        this.cards = new ArrayDeque<>(generate());
    }

    // generate a deck
    private List<Card> generate() {

        List<Card> deck = new ArrayList<>(DECK_SIZE);

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

        return deck;
    }

    public Card draw() {
        if (!deckShuffled) {
            shuffle();
        }

        if (cards.isEmpty()) {
            throw new NoSuchElementException("Deck is empty");
        }
        return cards.pop();
    }

    public void shuffle() {
        List<Card> list = new ArrayList<>(cards);
        Collections.shuffle(list);
        cards.clear();
        cards.addAll(list);
        deckShuffled = true;
    }

    public int remaining() {
        return cards.size();
    }
}
