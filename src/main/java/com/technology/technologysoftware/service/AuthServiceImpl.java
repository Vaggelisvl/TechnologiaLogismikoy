package com.technology.technologysoftware.service;

import com.technology.technologysoftware.auth.jwt.JwtUtils;
import com.technology.technologysoftware.domain.Role;
import com.technology.technologysoftware.domain.User;
import com.technology.technologysoftware.domain.UserRole;
import com.technology.technologysoftware.domain.request.login.LoginRequest;
import com.technology.technologysoftware.domain.request.registration.UserRegistrationRequest;
import com.technology.technologysoftware.domain.response.login.LoginResponse;
import com.technology.technologysoftware.repository.RoleRepository;
import com.technology.technologysoftware.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final AuthenticationManager authenticationManager;

    private static final String ROLE_NOT_FOUND_ERROR = "Error: Role has not be found.";
    private final UserRepository userRepository;


    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                           RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        log.info("AuthServiceImpl::authenticateUser() LoginRequest : {}",loginRequest);
        log.debug("LoginRequest : {}",loginRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @Override
    public ResponseEntity<?> registerUser(UserRegistrationRequest userRegistrationRequest) {
        log.info("AuthServiceImpl::registerUser() UserRegistrationRequest : {}",userRegistrationRequest);
        if (TRUE.equals(userRepository.existsByUsername(userRegistrationRequest.getUsername()))) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        if (TRUE.equals(userRepository.existsByEmail(userRegistrationRequest.getEmail()))) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user's account
        User user =User.builder().username(userRegistrationRequest.getUsername()).
                email(userRegistrationRequest.getEmail()).password(encoder.encode(userRegistrationRequest.getPassword())).build();


        Set<String> strRoles = userRegistrationRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (isNull(strRoles)) {
            Role userRole = roleRepository.findByName(UserRole.VISITOR)
                    .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(UserRole.ADMIN)
                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
                        roles.add(adminRole);

                        break;
                    case "regular":
                        Role modRole = roleRepository.findByName(UserRole.REGULAR)
                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(UserRole.VISITOR)
                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
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

    @Override
    public String init() {
        roleRepository.save(Role.builder().name(UserRole.VISITOR).id(1).build());
        roleRepository.save(Role.builder().name(UserRole.ADMIN).id(2).build());
        roleRepository.save(Role.builder().name(UserRole.REGULAR).id(3).build());
        return "Initialization of roles ok";
    }


}
