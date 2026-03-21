package com.cassinocards.cassino_api.shared.exception;

import java.util.UUID;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(UUID token) {
        super("Invalid or expired token: " + token);
    }
}
