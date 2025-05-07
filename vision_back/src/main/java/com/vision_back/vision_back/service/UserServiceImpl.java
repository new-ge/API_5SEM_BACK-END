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

    @Autowired
    private UserProjectHelperServiceImpl taigaHelper;
    
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
        return taigaHelper.fetchLoggedUserId();
    }
    
    @Override
    public void processAllUsers() {
        Integer projectId = taigaHelper.fetchProjectIdByUserId(taigaHelper.fetchLoggedUserId());
        taigaHelper.processUsersByProjectId(projectId);
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
