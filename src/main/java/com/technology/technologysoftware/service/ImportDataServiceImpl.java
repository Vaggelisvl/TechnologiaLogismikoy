package com.technology.technologysoftware.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.technology.technologysoftware.auth.jwt.JwtUtils;
import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.PointOfInterest;
import com.technology.technologysoftware.domain.response.import_data.ImportDataResponse;
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

        try (CSVReader csvReader = createCsvReader(file)) {
            csvReader.readNext();

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                log.debug("Line: {}", Arrays.toString(row));
                PointOfInterest poi = processRow(row);
                if (poi != null) {
                    importedPOIs.add(poi);
                }
            }
        } catch (CsvValidationException ex) {
            log.error("Error Occurred: {}", ex.getMessage());
        }

        this.objectToJson(importedPOIs);
        pointOfInterestRepository.saveAll(importedPOIs);

        return createImportDataResponse(importedPOIs);
    }

    private CSVReader createCsvReader(MultipartFile file) throws IOException {
        return new CSVReaderBuilder(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                .withCSVParser(new CSVParserBuilder().withSeparator('\t').build())
                .build();
    }

    private PointOfInterest processRow(String[] row) {
        long timestampAdded = parseLong(row[0]);
        if (timestampAdded == -1) {
            return null;
        }

        String title = row[1].trim();
        if (title.isEmpty()) {
            log.error("Title is empty");
            return null;
        }

        String description = row[2].trim();

        double latitude = parseDouble(row[3]);
        if (latitude == -1) {
            return null;
        }

        double longitude = parseDouble(row[4]);
        if (longitude == -1) {
            return null;
        }

        List<String> keywords = Arrays.stream(row[5].split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<String> categoriesStringList = Arrays.stream(row[6].split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<Category> categoryList = this.assingPoisCatories(categoriesStringList);

        return createPoi(timestampAdded, title, description, latitude, longitude, keywords, categoryList);
    }

    private long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
            return -1;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
            return -1;
        }
    }

    private PointOfInterest createPoi(long timestampAdded, String title, String description, double latitude, double longitude, List<String> keywords, List<Category> categoryList) {
        return new PointOfInterest(timestampAdded, title, description, latitude, longitude, keywords, categoryList);
    }

    private ImportDataResponse createImportDataResponse(List<PointOfInterest> importedPOIs) {
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
        List<ImportDataResponse.DataItem> categoryDataList = importedCategories.stream().map(ImportDataResponse.CategoryData::new).collect(Collectors.toList());
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

    @Override
    public boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public List<Category> assingPoisCatories(List<String> categories) {
        List<Category> categoryList = new ArrayList<>();
        if (categories != null) {
            List<String> categoriesName = processCategoryNames(categories);
            List<Integer> categoriesId = processCategoryIds(categories);
            addCategoriesByName(categoryList, categoriesName);
            addCategoriesById(categoryList, categoriesId);
        }
        return categoryList;
    }

    private List<String> processCategoryNames(List<String> categories) {
        return categories.stream()
                .filter(category -> !isInteger(category))
                .collect(Collectors.toList());
    }

    private List<Integer> processCategoryIds(List<String> categories) {
        return categories.stream()
                .filter(this::isInteger)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private void addCategoriesByName(List<Category> categoryList, List<String> categoriesName) {
        categoriesName.forEach(categoryName -> categoryRepository.findByName(categoryName).ifPresentOrElse(categoryList::add,
                () -> log.error("Category with name:{} does not exist", categoryName)));
    }

    private void addCategoriesById(List<Category> categoryList, List<Integer> categoriesId) {
        categoriesId.forEach(categoryId -> categoryRepository.findById(categoryId).ifPresentOrElse(categoryList::add,
                () -> log.error("Category with id:{} does not exist", categoryId)));
    }

    @Override
    public <T> void objectToJson(List<T> classList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            for (T className : classList) {
                String json = gson.toJson(className);
                log.info("\n{}\n Poi: {} \n", "-".repeat(45), json + "\n" + "-".repeat(45));
            }
        } catch (Exception e) {
           log.error("Error occurred: {}", e.getMessage());
        }
    }


}
