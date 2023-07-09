package com.technology.technologysoftware.controller;

import com.technology.technologysoftware.domain.request.login.LoginRequest;
import com.technology.technologysoftware.domain.request.registration.UserRegistrationRequest;
import com.technology.technologysoftware.domain.response.login.LoginResponse;
import com.technology.technologysoftware.domain.response.login.UnauthorizedErrorResponse;
import com.technology.technologysoftware.service.AuthService;
import com.technology.technologysoftware.service.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/map-services/auth")
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = LoginResponse.class))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = UnauthorizedErrorResponse.class))})
    })
    @Operation(description = "Log in operation")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);

    }

    @Operation(description = "Registration of new user operation")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegistrationRequest userRegistrationRequest) {
        return authService.registerUser(userRegistrationRequest);

    }

    @Operation(description = "Initialization of the roles document in database which register operation is using ")
    @PostMapping("/initialize")
    public String init() {
        return authService.init();
    }

}

