package com.cassinocards.cassino_api.dto.game.request;

import com.cassinocards.cassino_api.game.card.Card;
import com.cassinocards.cassino_api.model.user.User;

import java.util.List;

public record BuildRequestDTO(User user, Card handCard, List<Card> selectedTableCards) {}