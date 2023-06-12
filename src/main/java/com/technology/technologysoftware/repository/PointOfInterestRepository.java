package com.technology.technologysoftware.repository;


import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.PointOfInterest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PointOfInterestRepository extends MongoRepository<PointOfInterest, String> {

    List<PointOfInterest> findAllByTitleContainingAndKeywordsInAndCategoriesIn(String title, List<String> keywords, List<Category> categories);
}
