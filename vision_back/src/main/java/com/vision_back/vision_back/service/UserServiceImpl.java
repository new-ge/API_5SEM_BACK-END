package com.vision_back.vision_back.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;
import com.vision_back.vision_back.entity.PeriodEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.entity.dto.TokenDto;
import com.vision_back.vision_back.repository.PeriodRepository;
import com.vision_back.vision_back.repository.RoleRepository;
import com.vision_back.vision_back.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TokenDto tokenDto;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PeriodRepository periodRepository;
    
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
            saveOnDatabasePeriod(
                                 jsonNode.get("id").asInt(), 
                                 Integer.valueOf(Instant.parse(jsonNode.get("date_joined").asText()).atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))), 
                                 Integer.valueOf(Instant.parse(jsonNode.get("date_joined").asText()).atZone(ZoneId.systemDefault()).getMonthValue()), 
                                 Integer.valueOf(Instant.parse(jsonNode.get("date_joined").asText()).atZone(ZoneId.systemDefault()).getYear()),
                                 Integer.valueOf(Instant.parse(jsonNode.get("date_joined").asText()).atZone(ZoneId.systemDefault()).getHour())
                                );
            return jsonNode.get("id").asInt();
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar o Usuário", e);
        }
    }

    public UserEntity saveOnDatabaseUser(Integer userCode, String userDescription, String[] userRole, String userEmail) {
        try {
            return userRepository.findByUserCode(userCode)
            .orElseGet(() -> {
                UserEntity userEntity = new UserEntity(userCode, userDescription, userRole, userEmail);
                return userRepository.save(userEntity);
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }

    public PeriodEntity saveOnDatabasePeriod(Integer periodCode, Integer periodDate, Integer periodMonth, Integer periodYear, Integer periodHour) {
        try {
            return periodRepository.findByPeriodCode(periodCode)
            .orElseGet(() -> {
                PeriodEntity periodEntity = new PeriodEntity(periodCode, periodDate, periodMonth, periodYear, periodHour);
                return periodRepository.save(periodEntity);
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }
}
