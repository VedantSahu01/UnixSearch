package org.vedant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vedant.entity.SearchRequest;
import org.vedant.entity.SearchResponse;
import org.vedant.entity.StaxPost;
import org.vedant.entity.WikiPost;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class SearchService {

    @Autowired
    AsyncSearchService asyncSearchService;

    public SearchResponse search(SearchRequest request) {
        SearchResponse searchResponse = new SearchResponse();
        try {
        CompletableFuture<List<StaxPost>> completableStax = asyncSearchService.performStaxSearch(request);
        CompletableFuture<List<WikiPost>> completableWiki = asyncSearchService.performWikiSearch(request);
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(completableStax, completableWiki);
        combinedFuture.get();
        List<StaxPost> staxResult = completableStax.get();
        List<WikiPost> wikiResult = completableWiki.get();
        searchResponse.setStaxPostList(staxResult);
        searchResponse.setWikiPostList(wikiResult);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return searchResponse;
    }

}
