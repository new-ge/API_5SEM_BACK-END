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
import com.vision_back.vision_back.entity.dto.UserDto;
import com.vision_back.vision_back.configuration.TokenConfiguration;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.repository.MilestoneRepository;
import com.vision_back.vision_back.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TokenConfiguration tokenDto;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;
    
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
    
    @Transactional
    @Override
    public Integer getUserId() {
        setHeadersProject();

        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/users/me", HttpMethod.GET, headersEntity, String.class);
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            saveOnDatabaseUser(jsonNode.get("id").asInt(), jsonNode.get("username").asText(), objectMapper.convertValue(jsonNode.get("roles"), String[].class), jsonNode.get("email").asText(), 1);
            return jsonNode.get("id").asInt();
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar o Usuário", e);
        }
    }

    public List<Map<String, Object>> getUsersAndTasks(Integer projectId) {
            setHeadersProject();

            ResponseEntity<String> userResponse = restTemplate.exchange("https://api.taiga.io/api/v1/users?project=" 
                    + projectId, HttpMethod.GET, headersEntity, String.class);

            List<Map<String, Object>> usersList = new ArrayList<>();

            try {
                JsonNode usersJsonNode = objectMapper.readTree(userResponse.getBody());

                ResponseEntity<String> taskResponse = restTemplate.exchange("https://api.taiga.io/api/v1/tasks?project=" 
                        + projectId, HttpMethod.GET, headersEntity, String.class);

                JsonNode tasksJsonNode = objectMapper.readTree(taskResponse.getBody());

                for (JsonNode userNode : usersJsonNode) {
                    Integer userId = userNode.get("id").asInt();
                    String userName = userNode.hasNonNull("username") ? userNode.get("username").asText() : "N/A";
                    String userEmail = userNode.hasNonNull("email") ? userNode.get("email").asText() : "N/A";
                    List<String> userRole = new ArrayList<>();
                    if (userNode.hasNonNull("role")) {
                        JsonNode rolesNode = userNode.get("role");
                        if (rolesNode.isArray()) {
                            for (JsonNode roleNode : rolesNode) {
                            userRole.add(roleNode.asText()); 
                            }
                        }
                    }


                    long taskCount = 0;

                    for (JsonNode taskNode : tasksJsonNode){
                        if (taskNode.hasNonNull("assigned_to") && taskNode.get("assigned_to").asInt() == userId) {
                            taskCount++;    
                        }
                    }

                    Map<String, Object> userWithTaskCount = new HashMap<>();
                    userWithTaskCount.put("user", new UserDto(userId, userName, userRole, userEmail));
                    userWithTaskCount.put("taskCount", taskCount); 

                    usersList.add(userWithTaskCount);
                }

                return usersList;
            } catch (Exception e) {
                throw new IllegalArgumentException("Erro ao retornar usuários ou tasks", e);
        }
   }

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
           List<String> userRole = new ArrayList<>();
           if (userNode.hasNonNull("role")) {
               JsonNode rolesNode = userNode.get("role");
               if (rolesNode.isArray()) {
                   for (JsonNode roleNode : rolesNode) {
                   userRole.add(roleNode.asText()); 
                   }
               }
           }

            long taskCount = 0;

            for (JsonNode taskNode : tasksJsonNode) {
                if (taskNode.hasNonNull("assigned_to") && taskNode.get("assigned_to").asInt() == userId) {
                    String taskCreationDate = taskNode.get("created_date").asText();
                    if (taskCreationDate.compareTo(sprintStartDate) >= 0 && taskCreationDate.compareTo(sprintEndDate) <= 0) {
                        taskCount++;
                    }
                }
            }

            Map<String, Object> userWithTaskCount = new HashMap<>();
            userWithTaskCount.put("user", new UserDto(userId, userName, userRole, userEmail));
            userWithTaskCount.put("taskCount", taskCount);

            usersList.add(userWithTaskCount);
        }

        return usersList;

    } catch (Exception e) {
        throw new IllegalArgumentException("Erro ao retornar usuários ou tasks por sprint", e);
    }
 }

    @Transactional
    public void saveOnDatabaseUser(Integer userCode, String userDescription, String[] userRole, String userEmail, Integer isLogged) {
        System.out.println(!userRepository.existsByUserCodeAndUserNameAndUserRoleAndUserEmail(userCode, userDescription, userRole, userEmail));
        if (!userRepository.existsByUserCodeAndUserNameAndUserRoleAndUserEmail(userCode, userDescription, userRole, userEmail)) {
            UserEntity userEntity = new UserEntity(userCode, userDescription, userRole, userEmail, isLogged);
            userRepository.save(userEntity);
        } else {
            System.out.println("Chamando atualização do usuário...");
            if (userRepository.count() > 1) {
                userRepository.setAllUsersLoggedOut();
            }
            userRepository.updateIsLogged(isLogged, userCode);
        }
    }
}
