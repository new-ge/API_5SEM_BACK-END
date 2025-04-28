package com.vision_back.vision_back.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RestTemplate restTemplate;
    private String token;

    @Autowired
    public AuthenticationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
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

    @Override
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
        String userRole = rolesNode.get(0).asText();

        System.err.println(userRole);
        return userRole; // Retorna a primeira role
    }
}