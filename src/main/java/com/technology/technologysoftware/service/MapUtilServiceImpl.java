package com.technology.technologysoftware.service;


import com.technology.technologysoftware.auth.jwt.JwtUtils;
import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.PointOfInterest;
import com.technology.technologysoftware.domain.request.search_pois.SearchCriteria;
import com.technology.technologysoftware.domain.request.search_pois.SearchPoisRequest;
import com.technology.technologysoftware.domain.response.search_pois.SearchPoisResponse;
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

import static java.util.Objects.isNull;

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
        log.debug("MapUtilService::search() , SearchPoisRequest: {}", searchPoisRequest);

        SearchCriteria searchCriteria = buildSearchCriteria(searchPoisRequest);
        List<PointOfInterest> searchResults = performSearch(searchCriteria);

        if (!searchResults.isEmpty()) {
            List<PointOfInterest> filteredResults = searchResults;
            // Filter the results based on distance
            if (searchCriteria.getDistanceInMeters() > 0)
                filteredResults = filterByDistance(searchResults, searchCriteria.getDistanceInMeters());

            // Create the search response object
            SearchPoisResponse searchPoisResponse = new SearchPoisResponse();

            if (!isNull(searchPoisRequest.getStart())) searchPoisResponse.setStart(searchPoisRequest.getStart());
            if (!isNull(searchPoisRequest.getCount())) searchPoisResponse.setCount(searchPoisRequest.getCount());

            searchPoisResponse.setTotal(filteredResults.size());
            searchPoisResponse.setData(filteredResults);

            return ResponseEntity.ok(searchPoisResponse);
        }

        return null;
    }

    private SearchCriteria buildSearchCriteria(SearchPoisRequest searchPoisRequest) {
        SearchCriteria searchCriteria = new SearchCriteria();

        if (!isNull(searchPoisRequest.getText()))
            searchCriteria.setSearchText(searchPoisRequest.getText().toLowerCase());

        if (!isNull(searchPoisRequest.getFilters())) {
            if (!isNull(searchPoisRequest.getFilters().getDistance())) {
                double distanceInMeters = searchPoisRequest.getFilters().getDistance().getKm() * (double)1000;
                searchCriteria.setDistanceInMeters(distanceInMeters);
            }

            if (!isNull(searchPoisRequest.getFilters().getKeywords()))
                searchCriteria.setKeywords(searchPoisRequest.getFilters().getKeywords().stream().map(String::toLowerCase).collect(Collectors.toList()));

            if (!isNull(searchPoisRequest.getFilters().getCategories())) {
                List<Category> categoriesList = searchPoisRequest.getFilters().getCategories().stream()
                        .map(Integer::parseInt) // Convert each category ID string to an integer
                        .map(categoryRepository::findById) // Fetch the Category object for each ID
                        .filter(Optional::isPresent) // Filter out any non-existent categories
                        .map(Optional::get) // Get the actual Category object from the Optional
                        .collect(Collectors.toList());
                searchCriteria.setCategories(categoriesList);
            }
        }

        return searchCriteria;
    }

    private List<PointOfInterest> performSearch(SearchCriteria searchCriteria) {
        if (!isNull(searchCriteria.getSearchText()) && !isNull(searchCriteria.getKeywords()) && !isNull(searchCriteria.getCategories())) {
            return pointOfInterestRepository.findAllByTitleContainingIgnoreCaseAndKeywordsInIgnoreCaseAndCategoriesInIgnoreCase(searchCriteria.getSearchText(),
                    searchCriteria.getKeywords(), searchCriteria.getCategories());
        } else if (!isNull(searchCriteria.getCategories())) {
            return pointOfInterestRepository.findAllByCategoriesInIgnoreCase(searchCriteria.getCategories());
        } else if (!isNull(searchCriteria.getKeywords())) {
            return pointOfInterestRepository.findAllByKeywordsInIgnoreCase(searchCriteria.getKeywords());
        } else if (!isNull(searchCriteria.getSearchText())) {
            return pointOfInterestRepository.findAllByTitleInIgnoreCase(searchCriteria.getSearchText());
        } else {
            return pointOfInterestRepository.findAll();
        }
    }

    @Override
    public List<PointOfInterest> filterByDistance(List<PointOfInterest> pointsOfInterest, double distanceInMeters) {
        log.debug("MapUtilService::filterByDistance() , pointsOfInterest size: {} , distanceInMeters: {}", pointsOfInterest.size(), distanceInMeters);
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
        log.debug("MapUtilService::getAllCategories()");
        return categoryRepository.findAll();
    }


}
