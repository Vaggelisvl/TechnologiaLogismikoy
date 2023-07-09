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

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(value = TokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleTokenRefreshException(TokenException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.FORBIDDEN.value());

        if (ex.getCause() != null) {
            body.put("errors", ex.getCause().getMessage());
        } else {
            body.put("errors", ex.getMessage());
        }

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity<Object> handleNumberFormatException(NumberFormatException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());

        if (ex.getCause() != null) {
            body.put("errors", ex.getCause().getMessage());
        } else {
            body.put("errors", ex.getMessage());
        }

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleNumberFormatException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        if(ex.getBindingResult().getFieldError()!=null)
            body.put("errors", ex.getBindingResult().getFieldError().getDefaultMessage());
        else
            body.put("errors", ex.getMessage());


        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }



}
