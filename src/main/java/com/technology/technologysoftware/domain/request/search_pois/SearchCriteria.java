package com.technology.technologysoftware.domain.request.search_pois;

import com.technology.technologysoftware.domain.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class SearchCriteria {
    private String searchText;
    private double distanceInMeters;
    private List<String> keywords;
    private List<Category> categories;

}