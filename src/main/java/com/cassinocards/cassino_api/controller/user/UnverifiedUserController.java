package com.cassinocards.cassino_api.controller.user;

import com.cassinocards.cassino_api.model.user.UnverifiedUser;
import com.cassinocards.cassino_api.dto.user.response.AuthResponseDTO;
import com.cassinocards.cassino_api.dto.user.request.CreateUnverifiedUserDTO;
import com.cassinocards.cassino_api.dto.user.response.GetUnverifiedUserDTO;
import com.cassinocards.cassino_api.service.user.UnverifiedUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UnverifiedUserController {

    private final UnverifiedUserService unverifiedUserService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody CreateUnverifiedUserDTO dto) {
        unverifiedUserService.register(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponseDTO("Verification email sent to " + dto.email()));
    }

    @GetMapping("/verify")
    public ResponseEntity<GetUnverifiedUserDTO> get(@Valid @RequestParam("token") UUID token) {
        UnverifiedUser user = unverifiedUserService.find(token);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GetUnverifiedUserDTO(user.getEmail(), user.getVerificationToken()));
    }
}
