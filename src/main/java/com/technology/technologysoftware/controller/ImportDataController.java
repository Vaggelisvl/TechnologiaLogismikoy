package com.technology.technologysoftware.controller;


import com.technology.technologysoftware.domain.response.importData.ImportDataResponse;
import com.technology.technologysoftware.domain.response.login.LoginResponse;
import com.technology.technologysoftware.domain.response.login.UnauthorizedErrorResponse;
import com.technology.technologysoftware.service.ImportDataService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("api/v1/map-services/import")
public class ImportDataController {
    private final ImportDataService importDataService;

    public ImportDataController(ImportDataService importDataService) {
        this.importDataService = importDataService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ImportDataResponse.class))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = UnauthorizedErrorResponse.class))})
    })
    @PostMapping("/{type}")
    public ResponseEntity<?> importData(@RequestHeader("x-api-token") String token,@PathVariable String type, @RequestParam("file") MultipartFile file) {
        return importDataService.importData(token,type,file);
    }
}
