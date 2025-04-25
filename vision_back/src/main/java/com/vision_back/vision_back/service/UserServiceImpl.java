package com.vision_back.vision_back.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;
import com.vision_back.vision_back.configuration.TokenConfiguration;
import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.repository.RoleRepository;
import com.vision_back.vision_back.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TokenConfiguration tokenDto;

    @Autowired
    private UserRepository userRepository;
    
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    VisionBackApplication vba = new VisionBackApplication();
    HttpEntity<Void> headersEntity;

    @Override
    public HttpEntity<Void> setHeadersProject() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDto.getAuthToken());
            
        return headersEntity = new HttpEntity<>(headers);
    }

    @Override
    public Integer getUserId() {
        setHeadersProject();

        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/users/me", HttpMethod.GET, headersEntity, String.class);
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            saveOnDatabaseUser(jsonNode.get("id").asInt(), jsonNode.get("username").asText(), objectMapper.convertValue(jsonNode.get("roles"), String[].class), jsonNode.get("email").asText());
            return jsonNode.get("id").asInt();
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar o Usuário", e);
        }
    }

    @Transactional
    public UserEntity saveOnDatabaseUser(Integer userCode, String userDescription, String[] userRole, String userEmail) {
        Optional<UserEntity> existingUser = userRepository.findByUserCodeAndUserNameAndUserRoleAndUserEmail(userCode, userDescription, userRole, userEmail);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        UserEntity userEntity = new UserEntity(userCode, userDescription, userRole, userEmail);
        return userRepository.save(userEntity);
    }
}
