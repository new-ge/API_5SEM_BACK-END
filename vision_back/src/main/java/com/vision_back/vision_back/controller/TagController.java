package com.vision_back.vision_back.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vision_back.vision_back.service.TagService;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/card-count")
    public ResponseEntity<Map<Long, Integer>> getCardCounts() {
        Map<Long, Integer> cardCounts = tagService.getCardCountByTag();
        return ResponseEntity.ok(cardCounts);
    }
}

