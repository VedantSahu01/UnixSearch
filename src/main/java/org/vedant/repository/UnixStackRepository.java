package org.vedant.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.vedant.entity.StaxPost;

@Repository
public interface UnixStackRepository extends ElasticsearchRepository<StaxPost, String> {

}
