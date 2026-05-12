package com.cassinocards.cassino_api.game.player;

import com.cassinocards.cassino_api.game.card.Card;
import com.cassinocards.cassino_api.game.card.Hand;
import com.cassinocards.cassino_api.game.state.PlayerState;
import com.cassinocards.cassino_api.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class Player {
    private User user;
    private Hand hand;
    private List<Card> collection;
    private PlayerState state;
    private boolean lastCapturer;
    private int finalScore;

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

    public Optional<Card> getTopCollectionCard() {
        if (collection.isEmpty()) return Optional.empty();
        return Optional.of(collection.get(collection.size() - 1));
    }

    public void removeTopCollectionCard() {
        if (!collection.isEmpty()) {
            collection.remove(collection.size() - 1);
        }
    }
}
