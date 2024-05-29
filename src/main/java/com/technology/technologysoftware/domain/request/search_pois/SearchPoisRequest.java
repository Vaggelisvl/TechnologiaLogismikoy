package com.technology.technologysoftware.domain.request.search_pois;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SearchPoisRequest {

    @JsonProperty("start")
    private Integer start;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("text")
    private String text;

    @JsonProperty("filters")
    private Filter filters;

}
