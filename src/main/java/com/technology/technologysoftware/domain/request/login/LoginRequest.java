package com.technology.technologysoftware.domain.request.login;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @JsonProperty("userName")
    private String username;

    @JsonProperty("password")
    private String password;
}
