package com.technology.technologysoftware.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointOfInterest {

    @JsonProperty("timestampAdded")
    private long timestampAdded;


    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("keywords")
    private List<String> keywords;

    @JsonProperty("categories")
    private List<Category> categories;
}
