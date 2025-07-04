package com.github.argon.moduploader.core.browser;

public class BrowserException extends RuntimeException {
    public BrowserException() {
    }

    public BrowserException(String message) {
        super(message);
    }

    public BrowserException(String message, Throwable cause) {
        super(message, cause);
    }
}
