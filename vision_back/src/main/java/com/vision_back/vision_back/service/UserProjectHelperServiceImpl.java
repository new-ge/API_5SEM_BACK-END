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
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.repository.UserRepository;

@Service
public class UserProjectHelperServiceImpl implements UserProjectHelperService {

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpHeaders headers = new HttpHeaders();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService auth;

    private HttpEntity<?> setHeaders() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(auth.getCachedToken());

        return new HttpEntity<>(headers);
    }

    public Integer fetchLoggedUserId() {
        ResponseEntity<String> response = restTemplate.exchange(
            "https://api.taiga.io/api/v1/users/me",
            HttpMethod.GET,
            setHeaders(),
            String.class
        );

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            verifyIfIsLogged(jsonNode.get("id").asInt(), 1);
            return jsonNode.get("id").asInt();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar o Usuário", e);
        }
    }

    @Transactional
    public void processUsersByProjectId(Integer projectCode) {
        ResponseEntity<String> response = restTemplate.exchange(
            "https://api.taiga.io/api/v1/users?project=" + projectCode,
            HttpMethod.GET,
            setHeaders(),
            String.class
        );

        List<UserEntity> userEntities = new ArrayList<>();

        try {
            JsonNode usersArray = objectMapper.readTree(response.getBody());

            for (JsonNode userNode : usersArray) {
                Integer userCode = userNode.get("id").asInt();
                String username = userNode.get("username").asText();
                String[] roles = objectMapper.convertValue(userNode.get("roles"), String[].class);

                if (!userRepository.existsByUserCodeAndUserNameAndUserRole(userCode, username, roles)) {
                    userEntities.add(new UserEntity(userCode, username, roles, 1));
                }
            }

            userRepository.saveAll(userEntities);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar os usuários", e);
        }
    }

    public Integer fetchProjectIdByUserId(Integer userId) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                "https://api.taiga.io/api/v1/projects?member=" + userId,
                HttpMethod.GET,
                setHeaders(),
                String.class
            );
            JsonNode jsonNode = objectMapper.readTree(response.getBody()).get(0);
            return jsonNode.get("id").asInt();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
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
