package com.security.exception;

public class XSSServletException extends RuntimeException {

    public XSSServletException() {
    }

    public XSSServletException(String message) {
        super(message);
    }
}
