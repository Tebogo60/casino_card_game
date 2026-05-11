package com.cassinocards.cassino_api.game.player;

import com.cassinocards.cassino_api.game.card.Card;
import com.cassinocards.cassino_api.game.card.Hand;
import com.cassinocards.cassino_api.game.state.PlayerState;
import com.cassinocards.cassino_api.model.user.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Player {
    private User user;
    private Hand hand;
    private List<Card> collection;
    private PlayerState state;
    private boolean lastCapturer;

    public Player(User user) {
        this.user = user;
        this.hand = new Hand();
        this.collection = new ArrayList<>();
        this.state = PlayerState.WAITING;
        this.lastCapturer = false;
    }

    public void addToCollection(List<Card> cards) {
        collection.addAll(cards);
    }
}
