package com.cassinocards.cassino_api.dto.user;

public record LoginResponseDTO (
        String token,
        String email,
        String role
) {
}
