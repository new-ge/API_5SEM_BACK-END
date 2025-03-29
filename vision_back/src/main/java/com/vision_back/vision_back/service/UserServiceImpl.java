package com.vision_back.vision_back.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;

public class UserServiceImpl implements UserService {
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    VisionBackApplication vba = new VisionBackApplication();
    HttpEntity<Void> headersEntity;

    public HttpEntity<Void> setHeadersProject() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(vba.functionGetToken());
            
        return headersEntity = new HttpEntity<>(headers);
    }

    public List<Integer> getUserId(Integer projectId) {
        setHeadersProject();

        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/users?project="+projectId, HttpMethod.GET, headersEntity, String.class);
        List<Integer> listUserId = new ArrayList<>();

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            for (JsonNode ids : jsonNode) {
                Integer getUserId = ids.get("id").asInt();
                listUserId.add(getUserId);
            }
            return listUserId;

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar o Usuário", e);
        }
    }
}
