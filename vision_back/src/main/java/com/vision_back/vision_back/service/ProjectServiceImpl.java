package com.vision_back.vision_back.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;

public class ProjectServiceImpl {

    public String getProjectId() {
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        VisionBackApplication vba = new VisionBackApplication();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(vba.functionGetToken());
            
        HttpEntity<Void> headersEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects/by_slug?slug=newgeneration-git-teste", HttpMethod.GET, headersEntity, String.class);
            
        try {
            if (response.getBody().contains("id")) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                System.err.println(jsonNode);
                JsonNode getProjectId = jsonNode.get("id");
                return new ObjectMapper().writeValueAsString(getProjectId).replace("\"", "");
            } else {
                throw new NullPointerException("Erro 404: Resposta não obtida.");
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
