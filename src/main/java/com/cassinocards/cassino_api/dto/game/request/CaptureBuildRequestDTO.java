package com.cassinocards.cassino_api.dto.game.request;

import com.cassinocards.cassino_api.game.card.Card;
import com.cassinocards.cassino_api.model.user.User;

public record CaptureBuildRequestDTO(User user, Card handCard) {}