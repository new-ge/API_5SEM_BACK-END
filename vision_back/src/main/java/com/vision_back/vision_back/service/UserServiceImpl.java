package com.vision_back.vision_back.service;

import java.util.ArrayList;
import java.util.List;
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
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.repository.UserRepository;
import com.vision_back.vision_back.repository.UserTaskRepository;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationService auth;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;
    
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

    @Override
    public List<String> accessControl() {
        List<String> roles = new ArrayList<>();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                "https://api.taiga.io/api/v1/users/me",
                HttpMethod.GET,
                setHeadersProject(),
                String.class
            );
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            for (JsonNode roleNode : jsonNode.get("roles")) {
                roles.add(roleNode.asText());
            }

            return roles;
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao obter os papéis do usuário", e);
        }
    }    
}
