package com.example.exceptions;

public class FeedReaderException extends Exception {

    public FeedReaderException(String message) {
        super(message);
    }

    public FeedReaderException(String message, Throwable throwable) {
        super(message, throwable);
    }

}