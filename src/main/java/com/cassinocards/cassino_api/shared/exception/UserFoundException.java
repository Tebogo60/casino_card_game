package com.cassinocards.cassino_api.shared.exception;

public class UserFoundException extends RuntimeException {
    public UserFoundException(String email) {
        super("User already exists: " + email);
    }
}
