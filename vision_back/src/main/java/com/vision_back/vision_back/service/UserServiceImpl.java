package com.vision_back.vision_back.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;

public class UserServiceImpl implements UserService {
    ResponseEntity<String> response;
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    AuthenticationServiceImpl asImpl = new AuthenticationServiceImpl();
    HttpHeaders headers = new HttpHeaders();
    Dotenv dotenv = Dotenv.configure().filename("secrets.env").load();

    public String getUserId() {
        response = asImpl.consumeAuthentication(dotenv.get("PASSWORD_SECRET"), dotenv.get("USERNAME_SECRET"));

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode getUserId = jsonNode.get("id");
            System.out.println(new ObjectMapper().writeValueAsString(getUserId).replace("\"", ""));
            return new ObjectMapper().writeValueAsString(getUserId).replace("\"", "");

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar o Usuário", e);
        }
    }
}
