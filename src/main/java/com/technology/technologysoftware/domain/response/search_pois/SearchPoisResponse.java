package com.technology.technologysoftware.domain.response.search_pois;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.technology.technologysoftware.domain.PointOfInterest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchPoisResponse {

    @JsonProperty("start")
    private Integer start;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("data")
    private List<PointOfInterest> data;

}
