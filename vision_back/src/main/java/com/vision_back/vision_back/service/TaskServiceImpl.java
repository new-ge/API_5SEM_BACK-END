package com.vision_back.vision_back.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.text.html.HTML.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
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
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.TaskStatusHistoryEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.entity.TagEntity;
import com.vision_back.vision_back.entity.UserTaskEntity;
import com.vision_back.vision_back.entity.dto.TokenDto;
import com.vision_back.vision_back.repository.*;

import jakarta.transaction.Transactional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ProjectServiceImpl projectServiceImpl;

    @Autowired
    private TokenDto tokenDto;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository; 
    
    @Autowired
    private TagRepository userTagRepository; 

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TaskStatusHistoryRepository taskStatusHistoryRepository;

    @Autowired
    private RoleRepository userRoleRepository;

    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    VisionBackApplication vba = new VisionBackApplication();
    HttpEntity<Void> headersEntity;

    @Override
    public HttpEntity<Void> setHeadersTasks() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDto.getAuthToken());
            
        return new HttpEntity<>(headers);
    }

    @Override
    public Map<String, Integer> countTasksById() {
        setHeadersTasks();

        Integer userCode = userServiceImpl.getUserId();
        Integer projectCode = projectServiceImpl.getProjectId();

        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/tasks?project="+projectCode+"&assigned_to="+userCode, HttpMethod.GET, headersEntity, String.class);
        Map<String, Integer> statusCount = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            for (JsonNode node : rootNode) {
                saveOnDatabaseTask(Integer.valueOf(node.get("id").asText()), node.get("subject").asText());
                saveOnDatabaseStats(Integer.valueOf(node.get("status").asInt()), node.get("status_extra_info").get("name").asText());

                TaskEntity taskEntity = taskRepository.findByTaskCode(node.get("id").asInt()).orElseThrow(() -> new IllegalArgumentException("Task não encontrada"));
                StatusEntity statusEntity = statusRepository.findByStatusCodeAndStatusName(node.get("status").asInt(), node.get("status_extra_info").get("name").asText()).orElseThrow(() -> new IllegalArgumentException("Status não encontrado"));

                saveOnDatabaseTaskStatusHistory(taskEntity, statusEntity, Timestamp.from(Instant.parse(node.get("modified_date").asText())));

                String nameStatus = node.get("status_extra_info").get("name").asText();
                statusCount.put(nameStatus, statusCount.getOrDefault(nameStatus, 0) + 1);
            }

            return statusCount;

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar User Stories", e);
        }
    }
  
    @Override
    public int countCardsCreatedByDateRange(Integer userId, Integer projectId, String startDate, String endDate) {
        HttpEntity<Void> headersEntity = setHeadersTasks(); 
        
        String url = "https://api.taiga.io/api/v1/tasks?"
                   + "project=" + projectId + "&"
                   + "created_date__gte=" + startDate + "&"
                   + "created_date__lte=" + endDate;
    
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, headersEntity, String.class);
    
        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            for (JsonNode task : rootNode) {
                saveOnDatabaseTask(Integer.valueOf(task.get("id").asText()), task.get("subject").asText());
                saveOnDatabaseStats(Integer.valueOf(task.get("status").asInt()), task.get("status_extra_info").get("name").asText());

                TaskEntity taskEntity = taskRepository.findByTaskCode(task.get("id").asInt()).orElseThrow(() -> new IllegalArgumentException("Task não encontrada"));
                StatusEntity statusEntity = statusRepository.findByStatusCodeAndStatusName(task.get("status").asInt(), task.get("status_extra_info").get("name").asText()).orElseThrow(() -> new IllegalArgumentException("Status não encontrado"));

                saveOnDatabaseTaskStatusHistory(taskEntity, statusEntity, Timestamp.from(Instant.parse(task.get("modified_date").asText())));
            }
    
            return rootNode.size(); 
    
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar cards criados no período", e);
        }
    }
  
    @Override
    public Map<String, Integer> getTasksPerSprint(Integer userId, Integer projectId) {
        HttpEntity<Void> headersEntity = setHeadersTasks(); 
        Map<String, Integer> tasksPerSprint = new TreeMap<>();

        String sprintUrl = "https://api.taiga.io/api/v1/milestones?project=" + projectId;
        ResponseEntity<String> sprintResponse = restTemplate.exchange(sprintUrl, HttpMethod.GET, headersEntity, String.class);

        try {
            JsonNode sprints = objectMapper.readTree(sprintResponse.getBody());

            for (JsonNode sprint : sprints) {
                String sprintName = sprint.get("name").asText();
                String startDate = sprint.get("estimated_start").asText();
                String endDate = sprint.get("estimated_finish").asText();

                String taskUrl = "https://api.taiga.io/api/v1/tasks?"
                        + "assigned_to=" + userId + "&"
                        + "project=" + projectId + "&"
                        + "created_date__gte=" + startDate + "&"
                        + "created_date__lte=" + endDate;

                ResponseEntity<String> taskResponse = restTemplate.exchange(taskUrl, HttpMethod.GET, headersEntity, String.class);
                JsonNode tasks = objectMapper.readTree(taskResponse.getBody());

                for (JsonNode task : tasks) {
                    saveOnDatabaseTask(Integer.valueOf(task.get("id").asText()), task.get("subject").asText());
                    saveOnDatabaseStats(Integer.valueOf(task.get("status").asInt()), task.get("status_extra_info").get("name").asText());

                    TaskEntity taskEntity = taskRepository.findByTaskCode(task.get("id").asInt()).orElseThrow(() -> new IllegalArgumentException("Task não encontrada"));
                    StatusEntity statusEntity = statusRepository.findByStatusCodeAndStatusName(task.get("status").asInt(), task.get("status_extra_info").get("name").asText()).orElseThrow(() -> new IllegalArgumentException("Status não encontrado"));

                    saveOnDatabaseTaskStatusHistory(taskEntity, statusEntity, Timestamp.from(Instant.parse(task.get("modified_date").asText())));
                }

                tasksPerSprint.put(sprintName, tasks.size());
            }

            return tasksPerSprint;

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar tasks por sprint", e);
        }
    }

    @Override
    public Map<String, Integer> countTasksByStatusClosedBySprint(Integer userId, Integer projectId) {
        HttpEntity<Void> headersEntity = setHeadersTasks(); 
        Map<String, Integer> tasksPerSprint = new TreeMap<>();
        Integer sumClosed = 0;

        String sprintUrl = "https://api.taiga.io/api/v1/milestones?project=" + projectId;
        ResponseEntity<String> sprintResponse = restTemplate.exchange(sprintUrl, HttpMethod.GET, headersEntity, String.class);

        try {
            JsonNode sprints = objectMapper.readTree(sprintResponse.getBody());

            for (JsonNode sprint : sprints) {
                String sprintName = sprint.get("name").asText();
                String startDate = sprint.get("estimated_start").asText();
                String endDate = sprint.get("estimated_finish").asText();

                String taskUrl = "https://api.taiga.io/api/v1/tasks?"
                        + "project=" + projectId + "&"
                        + "assigned_to=" + userId + "&"
                        + "created_date__gte=" + startDate + "&"
                        + "created_date__lte=" + endDate;

                ResponseEntity<String> taskResponse = restTemplate.exchange(taskUrl, HttpMethod.GET, headersEntity, String.class);
                JsonNode tasks = objectMapper.readTree(taskResponse.getBody());

                for (JsonNode node : tasks) {
                    TaskEntity taskEntity = saveOnDatabaseTask(Integer.valueOf(node.get("id").asText()), node.get("subject").asText());
                    StatusEntity statusEntity = saveOnDatabaseStats(Integer.valueOf(node.get("status").asInt()), node.get("status_extra_info").get("name").asText());
    
                    saveOnDatabaseTaskStatusHistory(taskEntity, statusEntity, Timestamp.from(Instant.parse(node.get("modified_date").asText())));

                    if ((node.get("status_extra_info").get("name").asText()).equals("Closed")){
                        sumClosed += 1;
                    } else { 
                        continue;
                    }
                } 

                tasksPerSprint.put(sprintName, sumClosed);
            }
            return tasksPerSprint;

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar tasks por sprint", e);
        }
    }

    @Override
    public Integer countTasksByStatusClosed(Integer projectId, Integer userId, String startDate, String endDate) {
        setHeadersTasks();

        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/tasks?project="+projectId+"&assigned_to="+userId + "&created_date__gte=" + startDate + "&created_date__lte=" + endDate, HttpMethod.GET, headersEntity, String.class);
        Integer sumClosed = 0;

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            for (JsonNode node : rootNode) {
                TaskEntity taskEntity = saveOnDatabaseTask(Integer.valueOf(node.get("id").asText()), node.get("subject").asText());
                StatusEntity statusEntity = saveOnDatabaseStats(Integer.valueOf(node.get("status").asInt()), node.get("status_extra_info").get("name").asText());

                saveOnDatabaseTaskStatusHistory(taskEntity, statusEntity, Timestamp.from(Instant.parse(node.get("modified_date").asText())));

                if ((node.get("status_extra_info").get("name").asText()).equals("Closed")){
                    sumClosed += 1;
                } else { 
                    continue;
                }
            } 
            return sumClosed;

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar User Stories", e);
        }
    } 

    @Override
    public Map<String, Integer> countTasksByTag() {
        setHeadersTasks();
        Integer projectCode = projectServiceImpl.getProjectId();
        Integer userCode = userServiceImpl.getUserId();

    
        ResponseEntity<String> response = restTemplate.exchange(
            "https://api.taiga.io/api/v1/tasks?project=" + projectCode + "&assigned_to=" + userCode, 
            HttpMethod.GET, 
            headersEntity, 
            String.class
        );
    
        Map<String, Integer> tagCount = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
    
            for (JsonNode node : rootNode) {
                Integer taskCode = node.get("id").asInt();
                saveOnDatabaseTask(taskCode, node.get("subject").asText());
                for (JsonNode tagNode : node.get("tags")) {
                    for (JsonNode tag : tagNode) {
                        if (!tag.isNull()) {
                            tagCount.put(tag.toString().replace("\"", ""), tagCount.getOrDefault(tag.toString().replace("\"", ""), 0) + 1);
                            TaskEntity taskEntity = taskRepository.findByTaskCode(taskCode).orElseThrow(() -> new IllegalArgumentException("Task não encontrada"));
                            ProjectEntity projectEntity = projectRepository.findByProjectCode(projectCode).orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
                            saveOnDatabaseTags(taskEntity, projectEntity, tag.asText(), 1);
                        }
                    }
                }
            }
            return tagCount;
    
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar as User Stories", e);
        }
    }

    public TaskEntity saveOnDatabaseTask(Integer taskCode, String taskDescription) {
        try {
            return taskRepository.findByTaskCode(taskCode)
            .orElseGet(() -> {
                TaskEntity taskEntity = new TaskEntity(taskCode, taskDescription);
                return taskRepository.save(taskEntity);
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }

    public TagEntity saveOnDatabaseTags(TaskEntity taskCode, ProjectEntity projectCode, String tagName, Integer quant) {
        try {
            return userTagRepository.findByTaskCodeAndProjectCodeAndTagNameAndQuant(taskCode, projectCode, tagName, quant)
            .orElseGet(() -> {
                TagEntity userTagEntity = new TagEntity(taskCode, projectCode, tagName, quant);
                return userTagRepository.save(userTagEntity);
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }

    public StatusEntity saveOnDatabaseStats(Integer statusCode, String statusName) {
        try {
            return statusRepository.findByStatusCodeAndStatusName(statusCode, statusName)
            .orElseGet(() -> {
                StatusEntity statusEntity = new StatusEntity(statusCode, statusName);
                return statusRepository.save(statusEntity);
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }

    public TaskStatusHistoryEntity saveOnDatabaseTaskStatusHistory(TaskEntity taskCode, StatusEntity statsCode, Timestamp changeDate) {
        try {
            return taskStatusHistoryRepository.findByTaskCodeAndStatsCodeAndChangeDate(taskCode, statsCode, changeDate)
            .orElseGet(() -> {
                TaskStatusHistoryEntity taskStatusHistoryEntity = new TaskStatusHistoryEntity(taskCode, statsCode, changeDate);
                return taskStatusHistoryRepository.save(taskStatusHistoryEntity);
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }

    public TaskStatusHistoryEntity saveOnDatabaseUserTask(TaskEntity taskCode, StatusEntity statsCode, Timestamp changeDate) {
        try {
            return taskStatusHistoryRepository.findByTaskCodeAndStatsCodeAndChangeDate(taskCode, statsCode, changeDate)
            .orElseGet(() -> {
                TaskStatusHistoryEntity taskStatusHistoryEntity = new TaskStatusHistoryEntity(taskCode, statsCode, changeDate);
                return taskStatusHistoryRepository.save(taskStatusHistoryEntity);
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }
}
