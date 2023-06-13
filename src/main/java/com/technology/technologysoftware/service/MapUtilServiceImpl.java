package com.technology.technologysoftware.service;


import com.technology.technologysoftware.auth.jwt.JwtUtils;
import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.PointOfInterest;
import com.technology.technologysoftware.domain.request.searchPois.Distance;
import com.technology.technologysoftware.domain.request.searchPois.SearchPoisRequest;
import com.technology.technologysoftware.domain.response.searchPois.SearchPoisResponse;
import com.technology.technologysoftware.repository.CategoryRepository;
import com.technology.technologysoftware.repository.PointOfInterestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MapUtilServiceImpl implements MapUtilService{
    private final CategoryRepository categoryRepository;
    private final PointOfInterestRepository pointOfInterestRepository;
    private final JwtUtils jwtUtils;

    public MapUtilServiceImpl(CategoryRepository categoryRepository, PointOfInterestRepository pointOfInterestRepository, JwtUtils jwtUtils) {
        this.categoryRepository = categoryRepository;
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public ResponseEntity<?> search(String token,SearchPoisRequest searchPoisRequest) {
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        log.info("MapUtilService::search() , SearchPoisRequest: {}",searchPoisRequest);
        // Extract filter parameters
        Distance distance = searchPoisRequest.getFilters().getDistance();
        List<String> keywords = searchPoisRequest.getFilters().getKeywords();
        List<String> categories = searchPoisRequest.getFilters().getCategories();
        String searchText = searchPoisRequest.getText();
        List<Category> categoriesList = categories.stream()
                .map(Integer::parseInt) // Convert each category ID string to an integer
                .map(categoryRepository::findById) // Fetch the Category object for each ID
                .filter(Optional::isPresent) // Filter out any non-existent categories
                .map(Optional::get) // Get the actual Category object from the Optional
                .collect(Collectors.toList());

        // Calculate distance in meters
        double distanceInMeters = distance.getKm() * 1000;

        // Perform the search using PointOfInterestRepository
        List<PointOfInterest> searchResults = pointOfInterestRepository.findAllByTitleContainingAndKeywordsInAndCategoriesIn(searchText, keywords, categoriesList);
        if (!searchResults.isEmpty()) {
            // Filter the results based on distance
            List<PointOfInterest> filteredResults = filterByDistance(searchResults, distanceInMeters);

            // Create the search response object
            SearchPoisResponse searchPoisResponse = new SearchPoisResponse();
            searchPoisResponse.setStart(searchPoisRequest.getStart());
            searchPoisResponse.setCount(searchPoisRequest.getCount());
            searchPoisResponse.setTotal(filteredResults.size());
            searchPoisResponse.setData(filteredResults);

            return  ResponseEntity.ok(searchPoisResponse);
        }

        return null;
    }

    @Override
    public List<PointOfInterest> filterByDistance(List<PointOfInterest> pointsOfInterest, double distanceInMeters) {
        log.info("MapUtilService::filterByDistance() , pointsOfInterest size: {} , distanceInMeters: {}",pointsOfInterest.size(),distanceInMeters);
        List<PointOfInterest> filteredResults = new ArrayList<>();

        for (PointOfInterest poi : pointsOfInterest) {
            double poiDistance = calculateDistance(poi.getLongitude(), poi.getLatitude());

            if (poiDistance <= distanceInMeters) {
                filteredResults.add(poi);
            }
        }

        return filteredResults;
    }

    @Override
    public double calculateDistance(double longitude, double latitude) {
        log.info("ImportDataService::calculateDistance(), longitude:{} , latitude:{}",longitude,latitude);
        // Implement your distance calculation logic here
        // This could be using a library or a custom implementation based on the formula you prefer
        // For simplicity, let's assume a basic distance calculation using longitude and latitude difference
        // You can replace this with your actual distance calculation logic
        double referenceLongitude = 0.0; // Reference longitude for distance calculation
        double referenceLatitude = 0.0;  // Reference latitude for distance calculation

        double longitudeDiff = Math.abs(referenceLongitude - longitude);
        double latitudeDiff = Math.abs(referenceLatitude - latitude);

        // Assuming a simple distance calculation using the difference in longitude and latitude
        return Math.sqrt(Math.pow(longitudeDiff, 2) + Math.pow(latitudeDiff, 2));
    }
}
