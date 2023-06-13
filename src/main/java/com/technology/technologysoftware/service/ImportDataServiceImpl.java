package com.technology.technologysoftware.service;

import com.technology.technologysoftware.auth.jwt.JwtUtils;
import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.PointOfInterest;
import com.technology.technologysoftware.domain.response.importData.ImportDataResponse;
import com.technology.technologysoftware.repository.CategoryRepository;
import com.technology.technologysoftware.repository.PointOfInterestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImportDataServiceImpl implements ImportDataService {
    private final JwtUtils jwtUtils;
    private final PointOfInterestRepository pointOfInterestRepository;
    private final CategoryRepository categoryRepository;

    public ImportDataServiceImpl(JwtUtils jwtUtils, PointOfInterestRepository pointOfInterestRepository, CategoryRepository categoryRepository) {
        this.jwtUtils = jwtUtils;
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ImportDataResponse importPOIs(MultipartFile file) throws IOException {
        log.info("ImportDataService::importPOIs() , MultipartFile: {}", file.getName());
        List<PointOfInterest> importedPOIs = new ArrayList<>();

        // Read the CSV file
        List<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream()))
                .lines().collect(Collectors.toList());

        // Validate and process each line
        for (String line : lines) {
            String[] fields = line.split("\t");
            log.info("Fields size:{}",fields.length);
            log.info("Lines: {}", line);
            // Perform validation based on the provided code
//            if (fields.length != 6) {
//                // Invalid number of fields
//                continue;
//            }

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

            Optional<String> description = Optional.of(fields[2].trim()).filter(s -> !s.isEmpty());

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

            Optional<List<String>> keywords = Optional.of(fields[5].trim())
                    .filter(s -> !s.isEmpty())
                    .map(s -> Arrays.asList(s.split(",")));

            PointOfInterest poi = new PointOfInterest(timestampAdded, title, description, latitude, longitude, keywords,
                    Optional.empty()); // Assuming categories will be empty for now

            // Save the valid POI to the database
            pointOfInterestRepository.save(poi);
            importedPOIs.add(poi);
        }
        ImportDataResponse importDataResponse = new ImportDataResponse();
        List<ImportDataResponse.DataItem> categoryDataList = importedPOIs.stream()
                .map(ImportDataResponse.PoiData::new)
                .collect(Collectors.toList());
        importDataResponse.setData(categoryDataList);
        return importDataResponse;

    }

    @Override
    public ImportDataResponse importCategories(MultipartFile file) throws IOException {
        log.info("ImportDataService::importCategories() , MultipartFile: {}", file.getName());
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
        categoryRepository.saveAll(importedCategories);
        ImportDataResponse importDataResponse = new ImportDataResponse();
        List<ImportDataResponse.DataItem> categoryDataList = importedCategories.stream()
                .map(ImportDataResponse.CategoryData::new)
                .collect(Collectors.toList());
        importDataResponse.setData(categoryDataList);
        return importDataResponse;
    }


    @Override
    public ResponseEntity<?> importData(String token, String type, MultipartFile file) {
        log.info("ImportDataService::importData(), token:{} , type:{} , MultipartFile:{}", token, type, file.getName());
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please provide a file");
        }
        try {

            if (type.equalsIgnoreCase("pois")) {

                return ResponseEntity.ok(this.importPOIs(file));
            } else if (type.equalsIgnoreCase("categories")) {
                return ResponseEntity.ok(this.importCategories(file));
            } else {
                return ResponseEntity.badRequest().build();
            }

        } catch (IOException e) {
            // Handle IO exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
