package com.cassinocards.cassino_api.controller.user;

import com.cassinocards.cassino_api.model.user.dto.AuthResponseDTO;
import com.cassinocards.cassino_api.model.user.dto.CreateUserDTO;
import com.cassinocards.cassino_api.model.user.dto.ForgotPasswordDTO;
import com.cassinocards.cassino_api.model.user.dto.ResetPasswordDTO;
import com.cassinocards.cassino_api.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/complete")
    public ResponseEntity<AuthResponseDTO> addUser(@Valid @RequestBody CreateUserDTO dto) {
        userService.save(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponseDTO("User created successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        userService.forgotPassword(dto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthResponseDTO("Password reset email sent"));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<AuthResponseDTO> resetPassword(
            @Valid @RequestBody ResetPasswordDTO dto) {
        userService.resetPassword(dto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthResponseDTO("Password reset successfully"));
    }
}
