package com.cassinocards.cassino_api.dto.user.response;

import java.util.UUID;

public record GetUnverifiedUserDTO (
        String email,
        UUID token
) {
}
