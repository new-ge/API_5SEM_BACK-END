package com.vision_back.vision_back.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;

public class UserStoriesServiceImpl {
    ResponseEntity<String> response;

    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    VisionBackApplication vba = new VisionBackApplication();
    HttpHeaders headers = new HttpHeaders();

    public ResponseEntity<String> consumeUserStories() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(vba.functionGetToken());
            
        HttpEntity<Void> headersEntity = new HttpEntity<>(headers);
        response = restTemplate.exchange("https://api.taiga.io/api/v1/userstories?project=1641986", HttpMethod.GET, headersEntity, String.class);
        return response;
    }

    public Map<String, Integer> countUserStoriesById() {
        Map<String, Integer> statusCount = new HashMap<>();
        consumeUserStories();

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            for (JsonNode node : rootNode) {
                String nameStatus = node.get("status_extra_info").get("name").asText();
                statusCount.put(nameStatus, statusCount.getOrDefault(nameStatus, 0) + 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusCount;
    }

}
