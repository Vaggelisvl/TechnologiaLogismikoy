package com.technology.technologysoftware.repository;

import com.technology.technologysoftware.domain.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category,Integer> {
    List<Category> findAllById(Integer id);
    Optional<Category> findByName(String name);
}
