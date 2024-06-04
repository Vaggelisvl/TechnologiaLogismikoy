package com.technology.technologysoftware.service;

import com.technology.technologysoftware.domain.request.login.LoginRequest;
import com.technology.technologysoftware.domain.request.registration.UserRegistrationRequest;
import org.springframework.http.ResponseEntity;

/**
 * Service interface for authentication and user registration.
 */
public interface AuthService {

    /**
     * Authenticates a user based on the provided login request.
     *
     * @param loginRequest The login request containing the user's credentials.
     * @return A ResponseEntity containing the authentication result. This could be a JWT token if authentication is successful, or an error message otherwise.
     */
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);

    /**
     * Initializes the authentication service. The specifics of this method will depend on the implementation.
     *
     * @return A string indicating the status of the initialization process.
     */
    String init();

    /**
     * Registers a new user based on the provided user registration request.
     *
     * @param userRegistrationRequest The user registration request containing the new user's details.
     * @return A ResponseEntity containing the registration result. This could be a success message if registration is successful, or an error message otherwise.
     */
    ResponseEntity<?> registerUser(UserRegistrationRequest userRegistrationRequest);
}