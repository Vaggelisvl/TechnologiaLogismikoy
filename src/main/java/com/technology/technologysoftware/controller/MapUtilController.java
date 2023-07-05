package com.technology.technologysoftware.controller;

import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.request.searchPois.SearchPoisRequest;
import com.technology.technologysoftware.domain.response.login.LoginResponse;
import com.technology.technologysoftware.domain.response.login.UnauthorizedErrorResponse;
import com.technology.technologysoftware.domain.response.searchPois.SearchPoisResponse;
import com.technology.technologysoftware.service.MapUtilService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/map-services/search")
public class MapUtilController {
    private final MapUtilService mapUtilService;

    public MapUtilController(MapUtilService mapUtilService) {
        this.mapUtilService = mapUtilService;
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = SearchPoisResponse.class))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = UnauthorizedErrorResponse.class))})
    })
    @PostMapping("/pois")
    public ResponseEntity<?> searchPois(@RequestHeader("x-api-token") String token, @RequestBody SearchPoisRequest request) {
        return mapUtilService.search(token, request);
    }
    @GetMapping("/categories")
    public List<Category> getCategories(){
        return mapUtilService.getAllCategories();
    }
}
