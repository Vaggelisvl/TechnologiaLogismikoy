package com.technology.technologysoftware.repository;


import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.PointOfInterest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PointOfInterestRepository extends MongoRepository<PointOfInterest, String> {

    List<PointOfInterest> findAllByTitleContainingIgnoreCaseAndKeywordsInIgnoreCaseAndCategoriesInIgnoreCase(String title, List<String> keywords, List<Category> categories);

}
