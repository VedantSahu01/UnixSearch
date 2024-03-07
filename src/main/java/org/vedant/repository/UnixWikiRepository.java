package org.vedant.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.vedant.entity.WikiPost;

@Repository
public interface UnixWikiRepository extends ElasticsearchRepository<WikiPost, String> {
}
