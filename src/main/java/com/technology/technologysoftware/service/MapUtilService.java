package com.technology.technologysoftware.service;

import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.PointOfInterest;
import com.technology.technologysoftware.domain.request.search_pois.SearchPoisRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MapUtilService {
    /**
     * Performs a search for Points of Interest (POIs) based on the provided search criteria.
     *
     * @param token The JWT token used for authentication.
     * @param searchPoisRequest The search criteria, encapsulated in a SearchPoisRequest object.
     * @return A ResponseEntity containing a SearchPoisResponse object if the search yields results, or null otherwise.
     *         If the provided JWT token is invalid, the method returns a ResponseEntity with an UNAUTHORIZED status.
     *         The SearchPoisResponse object contains the total number of results and a list of the results themselves.
     *         The search results are filtered based on distance if a distance filter is provided in the search criteria.
     *         The start and count fields of the SearchPoisResponse object are set based on the corresponding fields in the search criteria, if they are not null.
     */
    ResponseEntity<?> search(String token, SearchPoisRequest searchPoisRequest);

    /**
     * Filters a list of Points of Interest (POIs) based on a specified distance.
     *
     * @param pointsOfInterest The list of POIs to filter.
     * @param distanceInMeters The distance in meters to use as the filter.
     * @return A list of POIs that are within the specified distance.
     */
    List<PointOfInterest> filterByDistance(List<PointOfInterest> pointsOfInterest, double distanceInMeters);

    /**
     * Calculates the distance between a given point (specified by longitude and latitude) and a reference point.
     *
     * @param longitude The longitude of the point.
     * @param latitude The latitude of the point.
     * @return The calculated distance.
     */
    double calculateDistance(double longitude, double latitude);

    /**
     * Retrieves all categories from the repository.
     *
     * @return A list of all categories.
     */
    List<Category> getAllCategories();
}
