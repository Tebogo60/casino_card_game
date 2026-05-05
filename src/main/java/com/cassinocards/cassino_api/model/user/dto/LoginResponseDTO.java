package com.cassinocards.cassino_api.model.user.dto;

public record LoginResponseDTO (
        String token,
        String email,
        String password,
        String role
) {
}
