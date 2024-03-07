package org.vedant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.vedant.entity.SearchRequest;
import org.vedant.service.SearchService;

@Controller
@RequestMapping("/api/es")
public class SearchController {
    @Autowired
    SearchService searchService;

    @PostMapping("/search")
    public ResponseEntity<?> performSearch(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(searchService.search(searchRequest));
    }
}
