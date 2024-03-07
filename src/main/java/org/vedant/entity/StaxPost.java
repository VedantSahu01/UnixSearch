package org.vedant.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "unix_index")
@Data
public class StaxPost {
    @Id
    private int id;
    @Field
    private int postTypeId;
    @Field
    private int acceptedAnswerId;
    @Field
    private String creationDate;
    @Field
    private int score;
    @Field
    private int viewCount;
    @Field
    private String body;
    @Field
    private int ownerUserId;
    @Field
    private int lastEditorUserId;
    @Field
    private String lastEditDate;
    @Field
    private String lastActivityDate;
    @Field
    private String title;
    @Field
    private String tags;
    @Field
    private int answerCount;
    @Field
    private int commentCount;
    @Field
    private String contentLicense;
}
