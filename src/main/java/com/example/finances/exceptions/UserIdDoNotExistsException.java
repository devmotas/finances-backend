package com.example.finances.exceptions;

public class UserIdDoNotExistsException extends RuntimeException {
    public UserIdDoNotExistsException(String message) {
        super(message);
    }
}