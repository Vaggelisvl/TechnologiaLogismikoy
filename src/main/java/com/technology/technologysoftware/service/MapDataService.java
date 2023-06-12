package com.technology.technologysoftware.service;


import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.PointOfInterest;
import com.technology.technologysoftware.domain.request.searchPois.SearchPoisRequest;
import com.technology.technologysoftware.domain.response.searchPois.SearchPoisResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MapDataService {
    List<PointOfInterest> importPOIs(MultipartFile file) throws IOException;

    List<Category> importCategories(MultipartFile file) throws IOException;

    SearchPoisResponse search(SearchPoisRequest searchPoisRequest);

    List<PointOfInterest> filterByDistance(List<PointOfInterest> pointsOfInterest, double distanceInMeters);

    double calculateDistance(double longitude, double latitude);
}
