package com.cassinocards.cassino_api.dto.user;

import java.util.UUID;

public record GetUnverifiedUserDTO (
        String email,
        UUID token
) {
}
