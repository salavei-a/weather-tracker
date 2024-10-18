package com.asalavei.weathertracker.exception;

public class AlreadyExistsException extends AppRuntimeException {

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
