package com.github.argon.moduploader.vendor;

public class VendorException extends Exception {
    public VendorException() {
    }

    public VendorException(String message) {
        super(message);
    }

    public VendorException(String message, Throwable cause) {
        super(message, cause);
    }

    public VendorException(Throwable cause) {
        super(cause);
    }
}
