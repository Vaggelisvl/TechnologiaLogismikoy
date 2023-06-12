package com.technology.technologysoftware.repository;

import com.technology.technologysoftware.domain.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepository extends MongoRepository<Category,Integer> {
    List<Category> findAllById(Integer id);
}
