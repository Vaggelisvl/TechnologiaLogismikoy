package com.technology.technologysoftware.service;

import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.PointOfInterest;
import com.technology.technologysoftware.domain.request.searchPois.SearchPoisRequest;
import com.technology.technologysoftware.domain.response.searchPois.SearchPoisResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MapUtilService {
    ResponseEntity<?> search(String token, SearchPoisRequest searchPoisRequest);

    List<PointOfInterest> filterByDistance(List<PointOfInterest> pointsOfInterest, double distanceInMeters);

    double calculateDistance(double longitude, double latitude);
    List<Category> getAllCategories();
}
