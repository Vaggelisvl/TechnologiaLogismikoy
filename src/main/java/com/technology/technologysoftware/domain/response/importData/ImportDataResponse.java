package com.technology.technologysoftware.domain.response.importData;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.technology.technologysoftware.domain.Category;
import com.technology.technologysoftware.domain.PointOfInterest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportDataResponse {

    @JsonProperty("data")
    private List<DataItem> data;

    public interface DataItem {};


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PoiData implements DataItem {
        @JsonProperty("poi")
        private PointOfInterest poi;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryData implements DataItem {
        @JsonProperty("category")
        private Category category;
    }
}
