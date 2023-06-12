package com.technology.technologysoftware.domain.response.login;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnauthorizedErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;

    // Constructor, getters, and setters
}