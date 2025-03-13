package com.vision_back.vision_back.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserService {

    @Autowired
    private static AuthenticationServiceImpl auth;
    
        public String getUserId(String password, String username) {
        // public static void main(String[] args) throws JsonMappingException, JsonProcessingException, NullPointerException {
            // AuthenticationServiceImpl auth = new AuthenticationServiceImpl();
            ObjectMapper objectMapper = new ObjectMapper();
            RestTemplate restTemplate = new RestTemplate();
                    
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(auth.getTokenAuthentication("newge.2025", "newgeneration-git"));

            System.out.println(headers);
            
            HttpEntity<Void> headersEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/users/me", HttpMethod.GET, headersEntity, String.class);
            
            try {
                if (response.getBody().contains("id")) {
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    JsonNode getUserId = jsonNode.get("id");
                    String userId = new ObjectMapper().writeValueAsString(getUserId);
                    return userId.replace("\"", "");
                } else {
                    throw new NullPointerException("Erro 404: Resposta não obtida.");
                }
            } catch (Exception e) {
                return e.getMessage();
            }
    }
}
