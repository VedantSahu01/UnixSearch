package org.vedant.entity;

import lombok.Data;

import java.util.List;

@Data
public class FilterData {
    String fieldName;
    List<Object> filterValues;
}
