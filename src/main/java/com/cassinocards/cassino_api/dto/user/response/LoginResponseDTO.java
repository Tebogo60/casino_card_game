package com.cassinocards.cassino_api.dto.user.response;

public record LoginResponseDTO (
        String token,
        String email,
        String role
) {
}
