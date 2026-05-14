package com.cassinocards.cassino_api.dto.game;

import com.cassinocards.cassino_api.game.card.Card;

public record CardDTO(
        String suit,
        String rank,
        int value
) {
    public static CardDTO from(Card card) {
        return new CardDTO(
                card.getSuit().name(),
                card.getRank().name(),
                card.getValue()
        );
    }
}