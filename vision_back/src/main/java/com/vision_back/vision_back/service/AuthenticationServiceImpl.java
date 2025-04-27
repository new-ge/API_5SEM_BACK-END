package com.vision_back.vision_back.service;

<<<<<<< HEAD
<<<<<<< Updated upstream
=======
import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> 71c7d93dd61ff73b11b8badc3d6f324d3a2423e3
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
<<<<<<< HEAD
=======
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.vision_back.vision_back.service.AuthenticationService;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;
>>>>>>> Stashed changes
=======
import com.vision_back.vision_back.configuration.TokenConfiguration;
>>>>>>> 71c7d93dd61ff73b11b8badc3d6f324d3a2423e3

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

<<<<<<< HEAD
    private final RestTemplate restTemplate;
    private String token;

<<<<<<< Updated upstream
    public ResponseEntity<String> consumeAuthentication(String password, String username) {
=======
    @Autowired
    private TokenConfiguration tokenDto;

    @Autowired
    private TaskService processTaskStatsAndMilestone;
    
    @Override
    public void getTokenAuthentication(String password, String username) {
>>>>>>> 71c7d93dd61ff73b11b8badc3d6f324d3a2423e3

        headers.setContentType(MediaType.APPLICATION_JSON);
                
        String json = "{\"password\": \"" + password + "\"," +
                        "\"type\": \"normal\"," +
                        "\"username\": \"" + username + "\"}";
                
        HttpEntity<String> headersEntity = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/auth", HttpMethod.POST, headersEntity, String.class); 
        
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            tokenDto.setAuthToken(jsonNode.get("auth_token").asText());
            processTaskStatsAndMilestone.processTasksAndStatsAndMilestone();
        } catch (Exception e) {
            throw new NullPointerException("A resposta não existe ou não é possivel obter nenhum dado!");
        }
=======
    @Autowired
    public AuthenticationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
>>>>>>> Stashed changes
    }

    public String getTokenAuthentication(String password, String username) throws Exception {
        // Faz o login no Taiga e pega o token
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

        if (this.token == null || this.token.isEmpty()) {
            throw new Exception("Token de autenticação não encontrado");
        }

        return this.token;
    }

    public String authenticateAndGetRole(String username, String password) throws Exception {
        // Autentica e busca o token
        String token = getTokenAuthentication(password, username);

        // Usa o token para buscar o usuário
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

        // Processa a resposta para pegar a primeira role
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode rolesNode = root.path("roles");

        if (!rolesNode.isArray() || rolesNode.isEmpty()) {
            throw new Exception("Usuário não possui roles definidas");
        }
        String UseRole = rolesNode.get(0).asText();

        System.err.println(UseRole);
        return UseRole; // Retorna a primeira role
    }
}
