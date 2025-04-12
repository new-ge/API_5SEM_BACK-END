package com.vision_back.vision_back.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;
import com.vision_back.vision_back.entity.dto.UserDto;

@Service
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
    // estruturar uma lógica dentro do UserServiceImpl para pegar todos funcionários 
    //que fazem parte do projeto, trazer os seguintes dados: userId, nome, e-mail, roles
        //Vou ter que consumir a parte de tasks para identificar qual task pertence a tal funcionário. 
        //Os dois utilizam o projectId para unificar as informações
        //Quantificar quantidade tasks atribuídas a cada funcionário
        //usar o UserEntity e UserDto

    public List<Map<String, Object>> getUsersAndTasks(Integer projectId) {
            setHeadersProject();

            ResponseEntity<String> userResponse = restTemplate.exchange("https://api.taiga.io/api/v1/users?project=" 
                    + projectId, HttpMethod.GET, headersEntity, String.class);

            List<Map<String, Object>> usersList = new ArrayList<>();

            try {
                JsonNode usersJsonNode = objectMapper.readTree(userResponse.getBody());
                //System.out.println("Usuarios recebidos: " + usersJsonNode);  

                ResponseEntity<String> taskResponse = restTemplate.exchange("https://api.taiga.io/api/v1/tasks?project=" 
                        + projectId, HttpMethod.GET, headersEntity, String.class);

                JsonNode tasksJsonNode = objectMapper.readTree(taskResponse.getBody());
                System.out.println("Tarefas recebidas: " + tasksJsonNode);  // Print para ver as tasks


                for (JsonNode userNode : usersJsonNode) {
                    Integer userId = userNode.get("id").asInt();
                    String userName = userNode.get("username").asText();
                    String userEmail = userNode.get("email").asText();
                    Integer userRole = userNode.get("role").asInt();

                    long taskCount = 0;

                    for (JsonNode taskNode : tasksJsonNode){
                        if(taskNode.get("assigned_to").asInt() == userId){
                            taskCount++;
                        }
                    }

                    //System.out.println("Usuário: " + userName + " | Tarefas atribuídas: " + taskCount);

                    Map<String, Object> userWithTaskCount = new HashMap<>();
                    userWithTaskCount.put("user", new UserDto(userId,userName, userEmail,userRole)); // Retorna o UserDto
                    userWithTaskCount.put("taskCount", taskCount); 

                    usersList.add(userWithTaskCount);
                }

                return usersList;
            } catch (Exception e) {
                //System.out.println("Erro: " + e.getMessage()); 
                throw new IllegalArgumentException("Erro ao retornar usuários ou tasks", e);
        }
   } 
}
