package com.asalavei.weathertracker.system;

import com.asalavei.weathertracker.auth.SignInRequestDto;
import com.asalavei.weathertracker.exception.AuthenticationException;
import com.asalavei.weathertracker.exception.DatabaseOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.ui.Model;

import static com.asalavei.weathertracker.common.Constants.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException() {
        return ERROR_404_VIEW;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e) {
        log.error("Unexpected error occurred", e);
        return ERROR_500_VIEW;
    }

    @ExceptionHandler(DatabaseOperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseOperationException() {
        return ERROR_500_VIEW;
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAuthenticationException(AuthenticationException e, Model model) {
        model.addAttribute(ERROR_MESSAGE_ATTRIBUTE, e.getMessage());
        model.addAttribute(USER_ATTRIBUTE, new SignInRequestDto());
        return SIGNIN_VIEW;
    }
}
