package com.asalavei.weathertracker.exception;

public class DatabaseOperationException extends AppRuntimeException {

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
