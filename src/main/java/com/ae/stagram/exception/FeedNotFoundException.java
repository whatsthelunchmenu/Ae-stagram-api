package com.ae.stagram.exception;

public class FeedNotFoundException extends RuntimeException{

    public FeedNotFoundException(String message){
        super(message);
    }
}
