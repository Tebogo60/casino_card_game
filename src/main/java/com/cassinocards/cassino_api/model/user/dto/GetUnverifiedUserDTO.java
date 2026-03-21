package com.cassinocards.cassino_api.model.user.dto;

import java.util.UUID;

public record GetUnverifiedUserDTO (
        String email,
        UUID token
) {
}
