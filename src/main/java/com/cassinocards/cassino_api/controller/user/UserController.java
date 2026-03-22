package com.cassinocards.cassino_api.controller.user;

import com.cassinocards.cassino_api.model.user.dto.AuthResponseDTO;
import com.cassinocards.cassino_api.model.user.dto.CreateUserDTO;
import com.cassinocards.cassino_api.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
