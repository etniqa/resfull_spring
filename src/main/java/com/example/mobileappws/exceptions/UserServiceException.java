package com.example.mobileappws.exceptions;

public class UserServiceException extends RuntimeException {
    private static final long serialVersionUID = 9156342452704781180L;

    public UserServiceException(String message) {
        super(message);
    }
}
