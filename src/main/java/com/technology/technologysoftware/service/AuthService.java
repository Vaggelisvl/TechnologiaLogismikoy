package com.technology.technologysoftware.service;

import com.technology.technologysoftware.domain.request.login.LoginRequest;
import com.technology.technologysoftware.domain.request.registration.UserRegistrationRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);

    String init();

    ResponseEntity<?> registerUser(UserRegistrationRequest userRegistrationRequest);
}
