package org.vedant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

@Document(indexName = "unix_wiki_index")
@Data
public class WikiPost {
    @Id
    private String id;
    @Field
    private String title;
    @Field
    private String url;
    @Field("abstract")
    private String abstractText;
    @Field
    private List<LinkData> links;
}
