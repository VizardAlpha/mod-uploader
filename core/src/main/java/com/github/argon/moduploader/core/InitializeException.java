package com.github.argon.moduploader.core;

public class InitializeException extends RuntimeException {
    public InitializeException(String message) {
        super(message);
    }

    public InitializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializeException() {
    }
}
