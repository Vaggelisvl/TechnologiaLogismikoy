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
public class MapUtilServiceImpl implements MapUtilService {
    private final CategoryRepository categoryRepository;
    private final PointOfInterestRepository pointOfInterestRepository;
    private final JwtUtils jwtUtils;

    public MapUtilServiceImpl(CategoryRepository categoryRepository, PointOfInterestRepository pointOfInterestRepository, JwtUtils jwtUtils) {
        this.categoryRepository = categoryRepository;
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public ResponseEntity<?> search(String token, SearchPoisRequest searchPoisRequest) {
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        log.info("MapUtilService::search() , SearchPoisRequest: {}", searchPoisRequest);
        Distance distance = null;
        List<String> keywords = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        String searchText = null;
        List<Category> categoriesList = new ArrayList<>();
        double distanceInMeters = 0;
        List<PointOfInterest> searchResults = new ArrayList<>();

        if (searchPoisRequest.getText() != null)
            searchText = searchPoisRequest.getText().toLowerCase();


        if (searchPoisRequest.getFilters() != null) {

            if (searchPoisRequest.getFilters().getDistance() != null) {
                distance = searchPoisRequest.getFilters().getDistance();
                distanceInMeters = distance.getKm() * 1000;
                log.debug("distanceInMeters: {}", distanceInMeters);
            }


            if (searchPoisRequest.getFilters().getKeywords() != null)
                keywords = searchPoisRequest.getFilters().getKeywords().stream().map(String::toLowerCase).collect(Collectors.toList());

            if (searchPoisRequest.getFilters().getCategories() != null) {
                categories = searchPoisRequest.getFilters().getCategories();
                categoriesList = categories.stream()
                        .map(Integer::parseInt) // Convert each category ID string to an integer
                        .map(categoryRepository::findById) // Fetch the Category object for each ID
                        .filter(Optional::isPresent) // Filter out any non-existent categories
                        .map(Optional::get) // Get the actual Category object from the Optional
                        .collect(Collectors.toList());
                log.debug("Categories: {}", categoriesList);
            }
            if (searchPoisRequest.getFilters().getDistance() != null && searchPoisRequest.getFilters().getCategories() != null
                    && searchPoisRequest.getFilters().getKeywords() != null && searchPoisRequest.getText() != null) {

                log.debug("Searching with full body");
                searchResults = pointOfInterestRepository.findAllByTitleContainingIgnoreCaseAndKeywordsInIgnoreCaseAndCategoriesInIgnoreCase(searchText,
                        keywords, categoriesList);

            } else if (searchPoisRequest.getFilters().getDistance() == null && searchPoisRequest.getFilters().getCategories() != null
                    && searchPoisRequest.getFilters().getKeywords() == null) {

                log.debug("Searching with categories body");
                searchResults = pointOfInterestRepository.findAllByCategoriesInIgnoreCase(categoriesList);

            } else if (searchPoisRequest.getFilters().getDistance() == null && searchPoisRequest.getFilters().getCategories() == null
                    && searchPoisRequest.getFilters().getKeywords() != null && searchPoisRequest.getText() == null) {

                log.debug("Searching with keywords body");
                searchResults = pointOfInterestRepository.findAllByKeywordsInIgnoreCase(keywords);
            } else if (searchPoisRequest.getFilters().getCategories() != null && searchPoisRequest.getFilters().getKeywords() != null
                    && searchPoisRequest.getText() == null) {
                log.debug("Searching with keywords and categories and title body");
                searchResults = pointOfInterestRepository.findAllByKeywordsInIgnoreCaseAndCategoriesInIgnoreCase(keywords, categoriesList);
            }

        } else if (searchPoisRequest.getText() != null) {
            searchResults = pointOfInterestRepository.findAllByTitleInIgnoreCase(searchText);
            log.debug("Searching with title");
        } else {
            log.info("Searching with nothing");
            searchResults = pointOfInterestRepository.findAll();
        }


        if (!searchResults.isEmpty()) {
            List<PointOfInterest> filteredResults = searchResults;
            // Filter the results based on distance
            if (searchPoisRequest.getFilters() != null) {
                if (searchPoisRequest.getFilters().getDistance() != null)
                    filteredResults = filterByDistance(searchResults, distanceInMeters);

            }


            // Create the search response object
            SearchPoisResponse searchPoisResponse = new SearchPoisResponse();

            if (searchPoisRequest.getStart() != null)
                searchPoisResponse.setStart(searchPoisRequest.getStart());
            if (searchPoisRequest.getCount() != null)
                searchPoisResponse.setCount(searchPoisRequest.getCount());

            searchPoisResponse.setTotal(filteredResults.size());
            searchPoisResponse.setData(filteredResults);

            return ResponseEntity.ok(searchPoisResponse);
        }

        return null;
    }

    @Override
    public List<PointOfInterest> filterByDistance(List<PointOfInterest> pointsOfInterest, double distanceInMeters) {
        log.info("MapUtilService::filterByDistance() , pointsOfInterest size: {} , distanceInMeters: {}", pointsOfInterest.size(), distanceInMeters);
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
        log.info("ImportDataService::calculateDistance(), longitude:{} , latitude:{}", longitude, latitude);
        double referenceLongitude = 0.0; // Reference longitude for distance calculation
        double referenceLatitude = 0.0;  // Reference latitude for distance calculation

        double longitudeDiff = Math.abs(referenceLongitude - longitude);
        double latitudeDiff = Math.abs(referenceLatitude - latitude);

        // Assuming a simple distance calculation using the difference in longitude and latitude
        return Math.sqrt(Math.pow(longitudeDiff, 2) + Math.pow(latitudeDiff, 2));
    }

    @Override
    public List<Category> getAllCategories() {
        log.info("MapUtilService::getAllCategories()");
        return categoryRepository.findAll();
    }


}
