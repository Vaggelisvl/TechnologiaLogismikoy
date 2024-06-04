package com.technology.technologysoftware.service;

import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.response.import_data.ImportDataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
/**
 * Service interface for importing data.
 */
public interface ImportDataService {

    /**
     * Imports Points of Interest (POIs) from a file.
     *
     * @param file The file containing the POIs data.
     * @return An ImportDataResponse containing the results of the import operation.
     * @throws IOException If an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read.
     */
    ImportDataResponse importPOIs(MultipartFile file) throws IOException;

    /**
     * Imports categories from a file.
     *
     * @param file The file containing the categories data.
     * @return An ImportDataResponse containing the results of the import operation.
     * @throws IOException If an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read.
     */
    ImportDataResponse importCategories(MultipartFile file) throws IOException;

    /**
     * Imports data based on the specified type.
     *
     * @param token The JWT token used for authentication.
     * @param type The type of data to import (e.g., "POIs", "categories").
     * @param file The file containing the data to import.
     * @return A ResponseEntity containing the results of the import operation.
     */
    ResponseEntity<?> importData(String token, String type, MultipartFile file);

    /**
     * Checks if a string can be parsed to an integer.
     *
     * @param str The string to check.
     * @return true if the string can be parsed to an integer, false otherwise.
     */
    boolean isInteger(String str);

    /**
     * Assigns categories to POIs.
     *
     * @param categories The list of categories to assign.
     * @return A list of Category objects.
     */
    List<Category> assignPoisCategories(List<String> categories);

    /**
     * Converts a list of objects to JSON format.
     *
     * @param classList The list of objects to convert.
     * @param <T> The type of the objects in the list.
     */
    <T> void objectToJson(List<T> classList);
}
