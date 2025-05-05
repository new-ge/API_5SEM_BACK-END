package com.vision_back.vision_back.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor.STRING;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationService auth;

    @Autowired
    private UserRepository userRepository;
    
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    VisionBackApplication vba = new VisionBackApplication();
    HttpEntity<Void> headersEntity;

    @Override
    public HttpEntity<Void> setHeadersProject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(auth.getCachedToken());
            
        return new HttpEntity<>(headers);
    }

    @Override
    public String getUserRole() {
        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/users/me", HttpMethod.GET, setHeadersProject(), String.class);
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("roles").get(0).asText();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar o Usuário", e);
        }
    }
    
    @Override
    public Integer getUserId() {
        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/users/me", HttpMethod.GET, setHeadersProject(), String.class);
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("id").asInt();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar o Usuário", e);
        }
    }

    @Transactional
    @Override
    public void processUser() {
        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/users/me", HttpMethod.GET, setHeadersProject(), String.class);
        List<UserEntity> userEntities = new ArrayList<>();
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            if (!userRepository.existsByUserCodeAndUserNameAndUserRoleAndUserEmail(jsonNode.get("id").asInt(), jsonNode.get("username").asText(), objectMapper.convertValue(jsonNode.get("roles"), String[].class), jsonNode.get("email").asText())) {
                userEntities.add(new UserEntity(jsonNode.get("id").asInt(), jsonNode.get("username").asText(), objectMapper.convertValue(jsonNode.get("roles"), String[].class), jsonNode.get("email").asText(), 1));
            }
            userRepository.saveAll(userEntities);
            verifyIfIsLogged(jsonNode.get("id").asInt(), 1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar o Usuário", e);
        }
    }

    @Transactional
    @Override
    public void verifyIfIsLogged(Integer userCode, Integer isLogged) {
        if (userRepository.count() > 1) {
            userRepository.setAllUsersLoggedOut();
        }
        userRepository.updateIsLogged(isLogged, userCode);
    }
}
