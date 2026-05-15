package com.cassinocards.cassino_api.dto.game.response;

import com.cassinocards.cassino_api.game.rules.Build;

import java.util.List;

public record BuildDTO(
        int targetValue,
        String ownerUsername,
        List<CardDTO> cards
) {
    public static BuildDTO from(Build build) {
        return new BuildDTO(
                build.getTargetValue(),
                build.getOwner().getUser().getUsername(),
                build.getCards().stream().map(CardDTO::from).toList()
        );
    }
}
