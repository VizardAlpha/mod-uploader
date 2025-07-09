package com.github.argon.moduploader.core;

public class NotInitializedException extends RuntimeException {
    public NotInitializedException(String message) {
        super(message);
    }

    public NotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotInitializedException(Throwable cause) {
        super(cause);
    }
}
