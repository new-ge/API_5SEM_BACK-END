package com.vision_back.vision_back.service;

import java.util.HashMap;
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
import com.vision_back.vision_back.configuration.TokenConfiguration;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    ResponseEntity<String> response;

    @Autowired
    private TaskService processTaskStatsAndMilestone;

    private String token;

    @Autowired
    private TokenConfiguration tConf;

    @Override
    public String getTokenAuthentication(String password, String username) {
        try {
            String loginUrl = "https://api.taiga.io/api/v1/auth";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("type", "normal");
            body.put("username", username);
            body.put("password", password);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new Exception("Falha na autenticação: " + response.getStatusCode());
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            this.token = root.path("auth_token").asText();

            tConf.setAuthToken(token);

            if (this.token == null || this.token.isEmpty()) {
                throw new Exception("Token de autenticação não encontrado");
            }
            return token;
        } catch (Exception e) {
            throw new NullPointerException("Token indisponivel");
        }
    }
    
    @Override
    public String authenticateAndGetRole(String username, String password) {
        try {
            String token = getTokenAuthentication(password, username);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.taiga.io/api/v1/users/me",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new Exception("Erro ao buscar informações do usuário: " + response.getStatusCode());
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode rolesNode = root.path("roles");

            if (!rolesNode.isArray() || rolesNode.isEmpty()) {
                throw new Exception("Usuário não possui roles definidas");
            }
            String userRole = rolesNode.get(0).asText();

            processTaskStatsAndMilestone.processTasksAndStatsAndMilestone();

            System.err.println(userRole);
            return userRole;
        } catch (Exception e) {
            throw new NullPointerException("Role não encontrada");
        }
    }

}