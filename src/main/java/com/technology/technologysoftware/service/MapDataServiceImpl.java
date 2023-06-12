package com.technology.technologysoftware.service;


import com.mongodb.client.model.geojson.Point;
import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.PointOfInterest;
import com.technology.technologysoftware.domain.request.searchPois.Distance;
import com.technology.technologysoftware.domain.request.searchPois.SearchPoisRequest;
import com.technology.technologysoftware.domain.response.searchPois.SearchPoisResponse;
import com.technology.technologysoftware.repository.CategoryRepository;
import com.technology.technologysoftware.repository.PointOfInterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapDataServiceImpl implements MapDataService {

    private final PointOfInterestRepository pointOfInterestRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public List<PointOfInterest> importPOIs(MultipartFile file) throws IOException {
        List<PointOfInterest> importedPOIs = new ArrayList<>();

        // Read the CSV file
        List<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream()))
                .lines().collect(Collectors.toList());

        // Validate and process each line
        for (String line : lines) {
            String[] fields = line.split("\t");

            // Perform validation based on the provided code
            if (fields.length != 6) {
                // Invalid number of fields
                continue;
            }

            long timestampAdded;
            try {
                timestampAdded = Long.parseLong(fields[0]);
            } catch (NumberFormatException e) {
                // Invalid timestampAdded value
                continue;
            }

            String title = fields[1].trim();
            if (title.isEmpty()) {
                // Empty title
                continue;
            }

            Optional<String> description = Optional.ofNullable(fields[2].trim()).filter(s -> !s.isEmpty());

            double latitude;
            try {
                latitude = Double.parseDouble(fields[3]);
            } catch (NumberFormatException e) {
                // Invalid latitude value
                continue;
            }

            double longitude;
            try {
                longitude = Double.parseDouble(fields[4]);
            } catch (NumberFormatException e) {
                // Invalid longitude value
                continue;
            }

            Optional<List<String>> keywords = Optional.ofNullable(fields[5].trim())
                    .filter(s -> !s.isEmpty())
                    .map(s -> Arrays.asList(s.split(",")));

            PointOfInterest poi = new PointOfInterest(timestampAdded, title, description, latitude, longitude, keywords,
                    Optional.empty()); // Assuming categories will be empty for now

            // Save the valid POI to the database
            pointOfInterestRepository.save(poi);
            importedPOIs.add(poi);
        }

        return importedPOIs;
    }



    @Override
    public List<Category> importCategories(MultipartFile file) throws IOException {
        List<Category> importedCategories = new ArrayList<>();
        // Read the CSV file and process the data
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t");

                // Assuming the format of the CSV is: id, name
                if (fields.length >= 2) {
                    int id = Integer.parseInt(fields[0]);
                    String name = fields[1];

                    Category category = new Category(id, name);
                    importedCategories.add(category);
                }
            }
        }

        // Save the imported categories to the database

        return categoryRepository.saveAll(importedCategories);
    }

    @Override
    public SearchPoisResponse search(SearchPoisRequest searchPoisRequest) {
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

            return searchPoisResponse;
        }

        return null;
    }

    @Override
    public List<PointOfInterest> filterByDistance(List<PointOfInterest> pointsOfInterest, double distanceInMeters) {
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
