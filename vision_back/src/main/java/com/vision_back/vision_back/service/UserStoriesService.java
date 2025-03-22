package com.vision_back.vision_back.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface UserStoriesService {
    public ResponseEntity<String> consumeUserStories();

    public Map<String, Integer> countUserStoriesById();
}
