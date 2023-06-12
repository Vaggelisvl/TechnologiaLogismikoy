package com.technology.technologysoftware.controller;

import com.technology.technologysoftware.auth.jwt.JwtUtils;
import com.technology.technologysoftware.domain.Role;
import com.technology.technologysoftware.domain.User;
import com.technology.technologysoftware.domain.UserRole;
import com.technology.technologysoftware.domain.request.login.LoginRequest;
import com.technology.technologysoftware.domain.request.registration.UserRegistrationRequest;
import com.technology.technologysoftware.domain.response.login.LoginResponse;
import com.technology.technologysoftware.domain.response.login.UnauthorizedErrorResponse;
import com.technology.technologysoftware.repository.RoleRepository;
import com.technology.technologysoftware.repository.UserRepository;
import com.technology.technologysoftware.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/map-services/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;


    private final UserRepository userRepository;


    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = LoginResponse.class))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = UnauthorizedErrorResponse.class))})
    })

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        log.info("LoginRequest username: {} ,Password: {}", loginRequest.getUsername(), loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        if (userRepository.existsByUsername(userRegistrationRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(userRegistrationRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(userRegistrationRequest.getUsername(),
                userRegistrationRequest.getEmail(),
                encoder.encode(userRegistrationRequest.getPassword()));

        Set<String> strRoles = userRegistrationRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(UserRole.VISITOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(UserRole.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "regular":
                        Role modRole = roleRepository.findByName(UserRole.REGULAR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(UserRole.VISITOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        Random random = new Random();

        user.setRoles(roles);
        user.setId(random.nextLong());
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/initialize")
    public String init() {
        roleRepository.save(Role.builder().name(UserRole.VISITOR).id(1).build());
        roleRepository.save(Role.builder().name(UserRole.ADMIN).id(2).build());
        roleRepository.save(Role.builder().name(UserRole.REGULAR).id(3).build());
        return "Init ok";
    }

}

