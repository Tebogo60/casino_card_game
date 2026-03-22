package com.cassinocards.cassino_api.shared.exception;

import com.cassinocards.cassino_api.model.user.dto.AuthResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<AuthResponseDTO> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new AuthResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<AuthResponseDTO> handleInvalidToken(
            InvalidTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(UserFoundException.class)
    public ResponseEntity<AuthResponseDTO> handleUserNotFound(
            UserFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new AuthResponseDTO(ex.getMessage()));
    }
}
