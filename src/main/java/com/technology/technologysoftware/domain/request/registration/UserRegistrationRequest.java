package com.technology.technologysoftware.domain.request.registration;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("userName")
    private String username;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    @JsonProperty("email")
    private String email;

    @Column(length = 60)
    @JsonProperty("passWord")
    private String Password;

    private Set<String> roles;

}
