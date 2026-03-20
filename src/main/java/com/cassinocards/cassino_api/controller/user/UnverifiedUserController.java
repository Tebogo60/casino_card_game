package com.cassinocards.cassino_api.controller.user;

import com.cassinocards.cassino_api.model.user.dto.CreateUnverifiedUserDTO;
import com.cassinocards.cassino_api.service.user.UnverifiedUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UnverifiedUserController {

    private final UnverifiedUserService unverifiedUserService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody CreateUnverifiedUserDTO dto) {
        unverifiedUserService.register(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Verification email sent to " + dto.email()));
    }
}
