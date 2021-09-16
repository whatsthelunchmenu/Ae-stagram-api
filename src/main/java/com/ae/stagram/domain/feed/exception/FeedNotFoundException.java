package com.ae.stagram.domain.feed.exception;

public class FeedNotFoundException extends RuntimeException{

    public FeedNotFoundException(String message){
        super(message);
    }
}
