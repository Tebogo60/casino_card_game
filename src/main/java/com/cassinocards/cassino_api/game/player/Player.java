package com.cassinocards.cassino_api.game.player;

import com.cassinocards.cassino_api.game.card.Card;
import com.cassinocards.cassino_api.game.card.Hand;
import com.cassinocards.cassino_api.game.state.PlayerState;
import com.cassinocards.cassino_api.model.user.User;
import lombok.Getter;

import java.util.List;

@Getter
public class Player {
    private User user;
    private Hand hand;
    private List<Card> collection;
    private PlayerState state;
    private boolean lastCapturer;
}
