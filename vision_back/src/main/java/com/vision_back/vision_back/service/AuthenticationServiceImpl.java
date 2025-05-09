package com.vision_back.vision_back.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpHeaders headers = new HttpHeaders();

    private String cachedToken;
    private Instant tokenExpirationTime;
    
    @Override
    public String getTokenAuthentication(String password, String username) {
        
        headers.setContentType(MediaType.APPLICATION_JSON);
                
        String json = "{\"password\": \"" + password + "\"," +
                        "\"type\": \"normal\"," +
                        "\"username\": \"" + username + "\"}";

        HttpEntity<String> headersEntity = new HttpEntity<>(json, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/auth", HttpMethod.POST, headersEntity, String.class); 
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            cachedToken = jsonNode.get("auth_token").asText();

            tokenExpirationTime = Instant.now().plusSeconds(3600);
            return cachedToken;

        } catch (Exception e) {
            throw new NullPointerException("A resposta não existe ou não é possivel obter nenhum dado!");
        }
    }

    public String getCachedToken() {
        if (cachedToken == null || isTokenExpired()) {
            throw new IllegalStateException("Token não existe ou expirado. É necessário autenticar novamente.");
        }
        return cachedToken;
    }

    private boolean isTokenExpired() {
        return tokenExpirationTime == null || Instant.now().isAfter(tokenExpirationTime);
    }
}