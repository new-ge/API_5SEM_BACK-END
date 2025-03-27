package com.vision_back.vision_back.service;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    // public List<Integer> getUserId(Integer projectId) {
    //     setHeadersProject();

    //     ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/users?project="+projectId, HttpMethod.GET, headersEntity, String.class);
    //     List<Integer> listUserId = null;

    //     try {
    //         JsonNode jsonNode = objectMapper.readTree(response.getBody());
    //         for (JsonNode ids : jsonNode) {
    //             System.out.println(ids);
    //             JsonNode getUserId = ids.get("id");
    //             // System.out.println(ids.get("id"));
    //             System.out.println(getUserId);
    //             listUserId.add(getUserId);
    //         }
    //         // System.out.println(getUserId);
    //         // System.out.println(new ObjectMapper().writeValueAsString(getUserId).replace("\"", ""));
    //         // return new ObjectMapper().writeValueAsString(getUserId).replace("\"", "");
    //         return listUserId;

    //     } catch (Exception e) {
    //         throw new IllegalArgumentException("Erro ao processar o Usuário", e);
    //     }
    // }
}
