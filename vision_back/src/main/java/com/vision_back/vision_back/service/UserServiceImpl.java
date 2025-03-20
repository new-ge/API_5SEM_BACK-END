package com.vision_back.vision_back.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;

public class UserServiceImpl {
    public List<String> getUserId() {

        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        VisionBackApplication vba = new VisionBackApplication();
        HttpHeaders headers = new HttpHeaders();
    
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(vba.functionGetToken());

        HttpEntity<Void> headersEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/users?project=1641986", HttpMethod.GET, headersEntity, String.class);
           
        try {
            System.out.print("Retorno da API: " + response.getBody()); //Anotação apenas para verificação

            JsonNode JsonNode = objectMapper.readTree(response.getBody());

            //Elaboração da lista para armazenar todos os id's
            List<String> userIds = new ArrayList<>();

            if(JsonNode.isArray()){
                for (JsonNode user : JsonNode){
                    JsonNode idNode = user.get("id");
                    if (idNode != null){
                        String userId =idNode.asText();
                        System.out.println("ID extraída: " + userId);
                        userIds.add(userId);
                    }
                    
                }
                return userIds;
            } else{
                System.out.println("Erro: Resposta não é uma Lista. ");
                return new ArrayList<>();
            } 
        }catch (Exception e) {
                System.out.println("Erro ao processar a resposta da API: " + e.getMessage());
                return new ArrayList<>();}
        }
    
    
} 


//interface