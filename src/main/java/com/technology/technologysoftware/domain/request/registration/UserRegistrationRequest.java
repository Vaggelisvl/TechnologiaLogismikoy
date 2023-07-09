package com.technology.technologysoftware.domain.request.registration;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
    @Size(min = 5, message = "UserName must be at least 5 characters long")
    private String username;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE,message = "Invalid email address")
    @JsonProperty("email")
    private String email;
    @Valid
    @Column(length = 60)
    @JsonProperty("passWord")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String Password;

    private Set<String> roles;

}
