package com.technology.technologysoftware.repository;

import com.technology.technologysoftware.domain.Role;
import com.technology.technologysoftware.domain.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, Integer> {


    Optional<Role> findByName(UserRole name);

}
