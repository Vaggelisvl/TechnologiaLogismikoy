package com.technology.technologysoftware.controller;

import com.technology.technologysoftware.exception.TokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@ControllerAdvice
public class ExceptionHandlerController {

    private static final String ERROR_MESSAGE = "errors";
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";

    @ExceptionHandler(value = TokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleTokenRefreshException(TokenException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now().toString());
        body.put(STATUS, HttpStatus.FORBIDDEN.value());

        if (!isNull(ex.getCause())) {
            body.put(ERROR_MESSAGE, ex.getCause().getMessage());
        } else {
            body.put(ERROR_MESSAGE, ex.getMessage());
        }

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity<Object> handleNumberFormatException(NumberFormatException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now().toString());
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());

        if (!isNull(ex.getCause())) {
            body.put(ERROR_MESSAGE, ex.getCause().getMessage());
        } else {
            body.put(ERROR_MESSAGE, ex.getMessage());
        }

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleNumberFormatException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now().toString());
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        if (!isNull(ex.getBindingResult().getFieldError()))
            body.put(ERROR_MESSAGE, ex.getBindingResult().getFieldError().getDefaultMessage());
        else
            body.put(ERROR_MESSAGE, ex.getMessage());


        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }


}
