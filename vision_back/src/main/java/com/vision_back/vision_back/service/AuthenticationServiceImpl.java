package com.vision_back.vision_back.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthenticationServiceImpl {

    public String getTokenAuthentication(String password, String username) {

        Map<String, Object> jsonMap = null;
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
                
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
                
        String json = "{\"password\": \"" + password + "\"," +
                        "\"type\": \"normal\"," +
                        "\"username\": \"" + username + "\"}";
                
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        String response = restTemplate.postForObject("https://api.taiga.io/api/v1/auth", request, String.class);
        try {
            jsonMap = objectMapper.convertValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonMap != null && jsonMap.containsKey("auth_token")) {
            return (String) jsonMap.get("auth_token");
        } else {
            throw new NullPointerException("Erro 404: Resposta não obtida.");
        }

    }   
}