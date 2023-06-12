package com.technology.technologysoftware.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
    @Id
    private String id;
    @Field(name = "token")
    @Indexed(unique = true)
    private String token;

    @Field(name = "userId")
    @Indexed(unique = true)
    private Long userId;

    private Instant expiryDate;
}
