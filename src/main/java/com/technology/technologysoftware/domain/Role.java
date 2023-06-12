package com.technology.technologysoftware.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private UserRole name;


}
