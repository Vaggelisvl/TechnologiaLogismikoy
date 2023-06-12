package com.technology.technologysoftware.repository;


import com.technology.technologysoftware.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,Long> {
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    Optional<User> findByUsername(String userName);

}
