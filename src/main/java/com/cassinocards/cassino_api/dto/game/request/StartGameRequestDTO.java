package com.cassinocards.cassino_api.dto.game.request;

import com.cassinocards.cassino_api.model.user.User;

public record StartGameRequestDTO(User userOne, User userTwo) {}