package com.technology.technologysoftware.domain.response.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class LoginResponse {

    private String token;

    public LoginResponse(String accessToken) {
        this.token = accessToken;

    }
}
