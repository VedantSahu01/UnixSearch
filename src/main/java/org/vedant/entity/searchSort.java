package org.vedant.entity;

import lombok.Data;
import lombok.Getter;

@Data
public class searchSort {
    @Getter
    public enum SortEnum{
        ASC("asc"),
        DESC("desc");
        private final String value;
        SortEnum(String v){
            this.value = v;
        }
    }
    String field;
    SortEnum sortType;
}
