package org.vedant.entity;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {
    List<StaxPost> staxPostList;
    List<WikiPost> wikiPostList;
}
