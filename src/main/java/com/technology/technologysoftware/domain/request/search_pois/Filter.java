package com.technology.technologysoftware.domain.request.search_pois;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Filter {

    @JsonProperty("distance")
    private Distance distance;

    @JsonProperty("keywords")
    private List<String> keywords;

    @JsonProperty("categories")
    private List<String> categories;
}
