package org.vedant.entity;

import lombok.Data;

import java.util.List;

@Data
public class SearchRequest {
    private String searchTerm;
    private List<String> fields = null;
    private List<FilterData> filters = null;
    private searchSort sort = null;
    private Integer pageNum = 0;
    private Integer pageSize = 10;
}
