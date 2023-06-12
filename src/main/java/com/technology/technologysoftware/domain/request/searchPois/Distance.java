package com.technology.technologysoftware.domain.request.searchPois;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Distance {

    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("lon")
    private Double lon;

    @JsonProperty("km")
    private Integer km;
}
