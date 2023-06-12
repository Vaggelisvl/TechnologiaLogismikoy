package com.technology.technologysoftware.controller;


import com.technology.technologysoftware.auth.jwt.JwtUtils;
import com.technology.technologysoftware.service.MapDataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("api/v1/map-services/import")
public class ImportDataController {
    private final MapDataServiceImpl mapDataService;
    private final JwtUtils jwtUtils;

    public ImportDataController(MapDataServiceImpl mapDataService, JwtUtils jwtUtils) {
        this.mapDataService = mapDataService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/{type}")
    public ResponseEntity<?> importData(@RequestHeader("x-api-token") String token,@PathVariable String type, @RequestParam("file") MultipartFile file) {
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please provide a file");
        }
        try {

            if (type.equalsIgnoreCase("pois")) {
                return ResponseEntity.ok(mapDataService.importPOIs(file));
            } else if (type.equalsIgnoreCase("categories")) {
                return ResponseEntity.ok(mapDataService.importCategories(file));
            } else {
                return ResponseEntity.badRequest().build();
            }

        } catch (IOException e) {
            // Handle IO exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
