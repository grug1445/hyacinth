package com.hyacinth.exception;

/**
 * Created by feichen on 2018/10/29.
 */
public class HyacinthException extends RuntimeException {
    public HyacinthException(String message) {
        super(message);
    }

    public HyacinthException(String message, Throwable cause) {
        super(message, cause);
    }
}
