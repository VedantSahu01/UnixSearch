package org.vedant.service;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.vedant.entity.*;
import org.vedant.repository.UnixStackRepository;
import org.vedant.repository.UnixWikiRepository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncSearchService {

    @Autowired
    UnixStackRepository unixStackRepository;

    @Autowired
    UnixWikiRepository unixWikiRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Async
    CompletableFuture<List<StaxPost>> performStaxSearch(SearchRequest request){
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        List<String> staxFieldList = Arrays.stream(StaxPost.class.getFields()).map(Field::getName).toList();
        if(CollectionUtils.isEmpty(request.getFields())){
            queryBuilder.withQuery(QueryBuilders.queryString().query(request.getSearchTerm()).fields("*").fuzziness("AUTO").minimumShouldMatch("75%").build()._toQuery());
        } else{
            List<String> fields = request.getFields().stream().filter(staxFieldList::contains).toList();
            queryBuilder.withQuery(QueryBuilders.queryString().query(request.getSearchTerm()).fields(fields).fuzziness("AUTO").minimumShouldMatch("75%").build()._toQuery());
        }
        if(!CollectionUtils.isEmpty(request.getFilters())){
            for(FilterData filterData: request.getFilters()){
                if(staxFieldList.contains(filterData.getFieldName())){
                    queryBuilder.withFilter(QueryBuilders.termsSet().field(filterData.getFieldName()).terms(filterData.getFilterValues().toString()).minimumShouldMatchField(filterData.getFieldName()).build()._toQuery());
                }
            }
        }
        if(!Objects.isNull(request.getSort())){
            if(staxFieldList.contains(request.getSort().getField())){
                if(request.getSort().getSortType() == searchSort.SortEnum.DESC){
                    queryBuilder.withSort(Sort.by(Sort.Direction.DESC, request.getSort().getField()));
                } else{
                    queryBuilder.withSort(Sort.by(Sort.Direction.ASC, request.getSort().getField()));
                }
            }
        }
        queryBuilder.withPageable(PageRequest.of(request.getPageNum(), request.getPageSize()));
        List<StaxPost> response = elasticsearchTemplate.search(queryBuilder.build(), StaxPost.class).stream()
                .map(SearchHit::getContent).toList();
        return CompletableFuture.completedFuture(response);
    }
    @Async
    CompletableFuture<List<WikiPost>> performWikiSearch(SearchRequest request){
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        List<String> wikiFieldList = Arrays.stream(WikiPost.class.getFields()).map(Field::getName).toList();
        if(CollectionUtils.isEmpty(request.getFields())){
            queryBuilder.withQuery(QueryBuilders.queryString().query(request.getSearchTerm()).fields("*").fuzziness("AUTO").minimumShouldMatch("75%").build()._toQuery());
        } else{
            List<String> fields = request.getFields().stream().filter(wikiFieldList::contains).toList();
            queryBuilder.withQuery(QueryBuilders.queryString().query(request.getSearchTerm()).fields(fields).fuzziness("AUTO").minimumShouldMatch("75%").build()._toQuery());
        }
        if(!CollectionUtils.isEmpty(request.getFilters())){
            for(FilterData filterData: request.getFilters()) {
                if (wikiFieldList.contains(filterData.getFieldName())) {
                    queryBuilder.withFilter(QueryBuilders.termsSet().field(filterData.getFieldName()).terms(filterData.getFilterValues().toString()).minimumShouldMatchField(filterData.getFieldName()).build()._toQuery());
                }
            }
        }
        if(!Objects.isNull(request.getSort())){
            if(wikiFieldList.contains(request.getSort().getField())) {
                if (request.getSort().getSortType() == searchSort.SortEnum.DESC) {
                    queryBuilder.withSort(Sort.by(Sort.Direction.DESC, request.getSort().getField()));
                } else {
                    queryBuilder.withSort(Sort.by(Sort.Direction.ASC, request.getSort().getField()));
                }
            }
        }
        queryBuilder.withPageable(PageRequest.of(request.getPageNum(), request.getPageSize()));
        List<WikiPost> response = elasticsearchTemplate.search(queryBuilder.build(), WikiPost.class).stream()
                .map(SearchHit::getContent).toList();
        return CompletableFuture.completedFuture(response);
    }
}
