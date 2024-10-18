package com.asalavei.weathertracker.handler;

import com.asalavei.weathertracker.dto.UserRequestDto;
import com.asalavei.weathertracker.exception.AlreadyExistsException;
import com.asalavei.weathertracker.exception.AuthenticationException;
import com.asalavei.weathertracker.exception.DatabaseOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.ui.Model;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException() {
        return "error/404";

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e) {
        log.error("Unexpected error occurred", e);
        return "error/500";
    }

    @ExceptionHandler(DatabaseOperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseOperationException() {
        return "error/500";
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAlreadyExistsException(AlreadyExistsException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("user", new UserRequestDto());
        return "auth/signup";
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAuthenticationException(AuthenticationException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("user", new UserRequestDto());
        return "auth/signin";
    }
}
