package com.vision_back.vision_back.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        //Os dois utilizam o projectId para unificar as informações
        //Quantificar quantidade tasks atribuídas a cada funcionário

    public List<Map<String, Object>> getUsersAndTasks(Integer projectId) {
            setHeadersProject();

            ResponseEntity<String> userResponse = restTemplate.exchange("https://api.taiga.io/api/v1/users?project=" 
                    + projectId, HttpMethod.GET, headersEntity, String.class);

            List<Map<String, Object>> usersList = new ArrayList<>();

            try {
                JsonNode usersJsonNode = objectMapper.readTree(userResponse.getBody());
                System.out.println("Usuarios recebidos: " + usersJsonNode);  

                ResponseEntity<String> taskResponse = restTemplate.exchange("https://api.taiga.io/api/v1/tasks?project=" 
                        + projectId, HttpMethod.GET, headersEntity, String.class);

                JsonNode tasksJsonNode = objectMapper.readTree(taskResponse.getBody());
                System.out.println("Tarefas recebidas: " + tasksJsonNode);  


                for (JsonNode userNode : usersJsonNode) {
                    Integer userId = userNode.get("id").asInt();
                    String userName = userNode.hasNonNull("username") ? userNode.get("username").asText() : "N/A";
                    String userEmail = userNode.hasNonNull("email") ? userNode.get("email").asText() : "N/A";
                    Integer userRole = userNode.hasNonNull("role") ? userNode.get("role").asInt() : -1;

                    long taskCount = 0;

                    for (JsonNode taskNode : tasksJsonNode){
                        if (taskNode.hasNonNull("assigned_to") && taskNode.get("assigned_to").asInt() == userId) {
                            taskCount++;    
                        }
                    }

                    System.out.println("Usuário: " + userName + " | Tarefas atribuídas: " + taskCount);

                    Map<String, Object> userWithTaskCount = new HashMap<>();
                    userWithTaskCount.put("user", new UserDto(userId,userName, userEmail,userRole));
                    userWithTaskCount.put("taskCount", taskCount); 

                    usersList.add(userWithTaskCount);
                }

                return usersList;
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage()); 
                throw new IllegalArgumentException("Erro ao retornar usuários ou tasks", e);
        }
   }
   //Contagem de tasks atribuídas ao usuário do projeto mas por sprint
   public List<Map<String, Object>> getUsersAndTasksPerSprintName(Integer projectId, String sprintName) {
    setHeadersProject();

    ResponseEntity<String> userResponse = restTemplate.exchange("https://api.taiga.io/api/v1/users?project=" 
    + projectId, HttpMethod.GET, headersEntity, String.class);

    List<Map<String, Object>> usersList = new ArrayList<>();

    try {
        JsonNode usersJsonNode = objectMapper.readTree(userResponse.getBody());

        ResponseEntity<String> taskResponse = restTemplate.exchange("https://api.taiga.io/api/v1/tasks?project=" 
        + projectId, HttpMethod.GET, headersEntity, String.class);
        JsonNode tasksJsonNode = objectMapper.readTree(taskResponse.getBody());

        ResponseEntity<String> sprintResponse = restTemplate.exchange("https://api.taiga.io/api/v1/milestones?project=" 
        + projectId, HttpMethod.GET, headersEntity, String.class);
        JsonNode sprintsJsonNode = objectMapper.readTree(sprintResponse.getBody());

        JsonNode selectedSprint = null;
        for (JsonNode sprint : sprintsJsonNode) {
            if (sprint.get("name").asText().equalsIgnoreCase(sprintName)) {
                selectedSprint = sprint;
                break;
            }
        }

        if (selectedSprint == null) {
            throw new IllegalArgumentException("Sprint com nome '" + sprintName + "' não encontrada no projeto " + projectId);
        }

        String sprintStartDate = selectedSprint.get("estimated_start").asText();
        String sprintEndDate = selectedSprint.get("estimated_finish").asText();

        for (JsonNode userNode : usersJsonNode) {
            Integer userId = userNode.get("id").asInt();
            String userName = userNode.hasNonNull("username") ? userNode.get("username").asText() : "N/A";
            String userEmail = userNode.hasNonNull("email") ? userNode.get("email").asText() : "N/A";
            Integer userRole = userNode.hasNonNull("role") ? userNode.get("role").asInt() : -1;

            long taskCount = 0;

            for (JsonNode taskNode : tasksJsonNode) {
                if (taskNode.hasNonNull("assigned_to") && taskNode.get("assigned_to").asInt() == userId) {
                    String taskCreationDate = taskNode.get("created_date").asText();
                    if (taskCreationDate.compareTo(sprintStartDate) >= 0 && taskCreationDate.compareTo(sprintEndDate) <= 0) {
                        taskCount++;
                    }
                }
            }

            System.out.println("Usuário: " + userName + " | Tarefas atribuídas: " + taskCount);

            Map<String, Object> userWithTaskCount = new HashMap<>();
            userWithTaskCount.put("user", new UserDto(userId, userName, userEmail, userRole));
            userWithTaskCount.put("taskCount", taskCount);

            usersList.add(userWithTaskCount);
        }

        return usersList;

    } catch (Exception e) {
        throw new IllegalArgumentException("Erro ao retornar usuários ou tasks por sprint", e);
    }
 }

}
