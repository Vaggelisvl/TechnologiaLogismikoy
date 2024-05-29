package com.technology.technologysoftware.service;

import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.response.import_data.ImportDataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImportDataService {
    ImportDataResponse importPOIs(MultipartFile file) throws IOException;

    ImportDataResponse importCategories(MultipartFile file) throws IOException;


    ResponseEntity<?> importData(String token, String type, MultipartFile file);

    boolean isInteger(String str);

    List<Category> assingPoisCatories(List<String> categories);


    <T> void objectToJson(List<T> classList);


}
