package com.ae.stagram.exception;

public class NotFoundToken extends RuntimeException{

    public NotFoundToken(String message) {
        super(message);
    }
}
