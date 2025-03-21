package com.vision_back.vision_back.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthenticationServiceImpl {
    public String getTokenAuthentication(String password, String username) {
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
                
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
                
        String json = "{\"password\": \"" + password + "\"," +
                        "\"type\": \"normal\"," +
                        "\"username\": \"" + username + "\"}";
                
        HttpEntity<String> headersEntity = new HttpEntity<>(json, headers);

        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/auth", HttpMethod.POST, headersEntity, String.class);
        
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode getAuthToken = jsonNode.get("auth_token");
            String userId = new ObjectMapper().writeValueAsString(getAuthToken);
            return userId.replace("\"", "");
        } catch (Exception e) {
            throw new NullPointerException("A resposta não existe ou não é possivel obter nenhum dado!");
        }
    }
}