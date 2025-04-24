package com.vision_back.vision_back.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.vision_back.vision_back.configuration.TokenConfiguration;
import com.vision_back.vision_back.entity.MilestoneEntity;
import com.vision_back.vision_back.entity.PeriodEntity;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.RoleEntity;
import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.TaskStatusHistoryEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.entity.UserTaskEntity;
import com.vision_back.vision_back.entity.TagEntity;
import com.vision_back.vision_back.entity.dto.UserDto;
import com.vision_back.vision_back.entity.dto.UserTaskDto;
import com.vision_back.vision_back.repository.*;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ProjectServiceImpl projectServiceImpl;

    @Autowired
    private TokenConfiguration tokenDto;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository; 

    @Autowired
    private TaskRepository taskRepository; 

    @Autowired
    private StatusRepository statsRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private RoleRepository roleRepository; 
    
    @Autowired
    private TagRepository tagRepository; 

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskStatusHistoryRepository taskStatusHistoryRepository;

    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();

    @Override
    public HttpEntity<Void> setHeadersTasks() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDto.getAuthToken());
            
        return new HttpEntity<>(headers);
    }

    @Override
    public void processTaskUser(Integer projectCode, Integer taskCode, Integer userCode, Integer milestoneCode, Integer statsCode, Integer roleCode) {
        HttpEntity<Void> headersEntity = setHeadersTasks();

        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/tasks?project="+projectCode+"&assigned_to="+userCode, HttpMethod.GET, headersEntity, String.class);
        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            ProjectEntity projectEntity = projectRepository.findByProjectCode(projectCode).orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
            TaskEntity taskEntity = taskRepository.findByTaskCode(taskCode).orElseThrow(() -> new IllegalArgumentException("Task não encontrada"));
            UserEntity userEntity = userRepository.findByUserCode(userCode).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            StatusEntity statsEntity = statsRepository.findByStatusCode(statsCode).orElseThrow(() -> new IllegalArgumentException("Status não encontrado"));
            MilestoneEntity milestoneEntity = milestoneRepository.findByMilestoneCode(milestoneCode).orElseThrow(() -> new IllegalArgumentException("Milestone não encontrado"));
            RoleEntity roleEntity = roleRepository.findByRoleCode(roleCode).orElseThrow(() -> new IllegalArgumentException("Role não encontrada"));

            for (JsonNode node : rootNode) {
                if (node.get("id").asInt() == taskCode) {
                    JsonNode finishedDateNode = node.get("finished_date");
                    Timestamp finishedDate = null;
                    Timestamp createdDate = Timestamp.from(Instant.parse(node.get("created_date").asText()));
                    if (finishedDateNode != null && !finishedDateNode.isNull() && !finishedDateNode.asText().isEmpty()) {
                        finishedDate = Timestamp.from(Instant.parse(finishedDateNode.asText()));
                    }
                    saveOnDatabaseUserTask(taskEntity, projectEntity, userEntity, milestoneEntity, statsEntity, roleEntity, createdDate, finishedDate, 1);
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar User Stories", e);
        }
    }

    @Override
    public void processTaskHistory(Integer taskCode) {
        HttpEntity<Void> headersEntity = setHeadersTasks();

        ResponseEntity<String> responseTaskHistory = restTemplate.exchange("https://api.taiga.io/api/v1/history/task/"+taskCode, HttpMethod.GET, headersEntity, String.class);

        try {
            JsonNode rootNodeTaskHistory = objectMapper.readTree(responseTaskHistory.getBody());
            for (int i = rootNodeTaskHistory.size() - 1; i >= 0; i--) {
                JsonNode current = rootNodeTaskHistory.get(i);

                if (current.has("values_diff") && current.get("values_diff").has("status") && !current.get("values_diff").get("status").isNull()) {
                    String statusAtual = current.get("values_diff").get("status").get(1).asText();
                    String ultimoStatus = current.get("values_diff").get("status").get(0).asText();

                    TaskEntity taskEntity = taskRepository.findByTaskCode(taskCode).orElseThrow(() -> new IllegalArgumentException("Task não encontrada"));
                    
                    saveOnDatabaseTaskStatusHistory(taskEntity, ultimoStatus, statusAtual, Timestamp.from(Instant.parse(current.get("created_at").asText())));
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar User Stories", e);
        }
    }

    @Override
    public void processTasksAndStats() {
        HttpEntity<Void> headersEntity = setHeadersTasks();

        Integer userCode = userServiceImpl.getUserId();
        Integer projectCode = projectServiceImpl.getProjectId();

        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/tasks?project="+projectCode+"&assigned_to="+userCode, HttpMethod.GET, headersEntity, String.class);
        Map<String, Integer> statusCount = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            for (JsonNode node : rootNode) {
                Integer taskCode = node.get("id").asInt();
                Integer statusCode = node.get("status").asInt();
                saveOnDatabaseTask(taskCode, node.get("subject").asText());
                saveOnDatabaseStats(statusCode, node.get("status_extra_info").get("name").asText());
                processTaskHistory(node.get("id").asInt());
            }
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
                saveOnDatabaseTask(task.get("id").asInt(), task.get("subject").asText());
                saveOnDatabaseStats(task.get("status").asInt(), task.get("status_extra_info").get("name").asText());
                processTaskHistory(task.get("id").asInt());
            }
    
            return rootNode.size(); 
    
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar cards criados no período", e);
        }
    }

    @Override
    public void processTasksAndStatsAndMilestone() {
        HttpEntity<Void> headersEntity = setHeadersTasks(); 
        Map<String, Integer> tasksPerSprint = new TreeMap<>();

        Integer userCode = userServiceImpl.getUserId();
        Integer projectCode = projectServiceImpl.getProjectId();
        Integer roleCode = projectServiceImpl.getSpecificProjectUserRoleId();

        String sprintUrl = "https://api.taiga.io/api/v1/milestones?project=" + projectCode;
        ResponseEntity<String> sprintResponse = restTemplate.exchange(sprintUrl, HttpMethod.GET, headersEntity, String.class);

        try {
            JsonNode sprints = objectMapper.readTree(sprintResponse.getBody());

            for (JsonNode sprint : sprints) {

                Integer sprintCode = sprint.get("id").asInt();
                String sprintName = sprint.get("name").asText();
                String startDate = sprint.get("estimated_start").asText();
                String endDate = sprint.get("estimated_finish").asText();

                saveOnDatabaseMilestone(
                    sprint.get("id").asInt(), 
                    sprintName, 
                    LocalDate.parse(startDate), 
                    LocalDate.parse(endDate), 
                    projectRepository.findByProjectCode(projectCode).orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"))
                );

                String taskUrl = "https://api.taiga.io/api/v1/tasks?"
                        + "assigned_to=" + userCode + "&"
                        + "project=" + projectCode + "&"
                        + "created_date__gte=" + startDate + "&"
                        + "created_date__lte=" + endDate;

                ResponseEntity<String> taskResponse = restTemplate.exchange(taskUrl, HttpMethod.GET, headersEntity,
                        String.class);
                JsonNode tasks = objectMapper.readTree(taskResponse.getBody());

                for (JsonNode task : tasks) {
                    Integer taskCode = task.get("id").asInt();
                    Integer statusCode = task.get("status").asInt();
                    saveOnDatabaseTask(taskCode, task.get("subject").asText());
                    saveOnDatabaseStats(statusCode, task.get("status_extra_info").get("name").asText());
                    processTaskHistory(task.get("id").asInt());
                    processTaskUser(projectCode, taskCode, userCode, sprintCode, statusCode, roleCode);
                }
            }
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
        ResponseEntity<String> sprintResponse = restTemplate.exchange(sprintUrl, HttpMethod.GET, headersEntity,
                String.class);

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

                ResponseEntity<String> taskResponse = restTemplate.exchange(taskUrl, HttpMethod.GET, headersEntity,
                        String.class);
                JsonNode tasks = objectMapper.readTree(taskResponse.getBody());

                for (JsonNode node : tasks) {
                    saveOnDatabaseTask(Integer.valueOf(node.get("id").asText()), node.get("subject").asText());
                    saveOnDatabaseStats(Integer.valueOf(node.get("status").asInt()), node.get("status_extra_info").get("name").asText());
                    processTaskHistory(node.get("id").asInt());
                  

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
        HttpEntity<Void> headersEntity = setHeadersTasks();

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.taiga.io/api/v1/tasks?project=" + projectId + "&assigned_to=" + userId
                        + "&created_date__gte=" + startDate + "&created_date__lte=" + endDate,
                HttpMethod.GET, headersEntity, String.class);
        Integer sumClosed = 0;

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            for (JsonNode node : rootNode) {
                saveOnDatabaseTask(Integer.valueOf(node.get("id").asText()), node.get("subject").asText());
                saveOnDatabaseStats(Integer.valueOf(node.get("status").asInt()), node.get("status_extra_info").get("name").asText());
                processTaskHistory(node.get("id").asInt());

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
        HttpEntity<Void> headersEntity = setHeadersTasks();
        Integer projectCode = projectServiceImpl.getProjectId();
        Integer userCode = userServiceImpl.getUserId();
        String usernameString = "";
    
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
                usernameString = node.get("assigned_to_extra_info").get("username").asText();
                saveOnDatabaseTask(node.get("id").asInt(), node.get("subject").asText());

                if (node.get("assigned_to").asInt() == userCode) {
                    for (JsonNode tagArray : node.get("tags")) {
                        String tagName = tagArray.get(0).asText();
                        if (tagName != null && !tagName.isEmpty()) {
                            TaskEntity taskEntity = taskRepository.findByTaskCode(node.get("id").asInt()).orElseThrow(() -> new IllegalArgumentException("Task não encontrada"));
                            ProjectEntity projectEntity = projectRepository.findByProjectCode(projectCode).orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
                            saveOnDatabaseTags(taskEntity, projectEntity, tagName, 1);
                        }
                    }
                }

            }

            return tagCount;

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar as User Stories", e);
        }
    }
    public MilestoneEntity saveOnDatabaseMilestone(Integer milestoneCode, String milestoneName, LocalDate estimatedStart, LocalDate estimatedEnd, ProjectEntity projectCode) {
        try {            
            MilestoneEntity milestoneEntity = new MilestoneEntity(milestoneCode, milestoneName, estimatedStart, estimatedEnd, projectCode);
            return milestoneRepository.save(milestoneEntity);
        } catch (DataIntegrityViolationException e) {
            return milestoneRepository.findByMilestoneCodeAndMilestoneNameAndEstimatedStartAndEstimatedEnd(milestoneCode, milestoneName, estimatedStart, estimatedEnd)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao recuperar milestone após falha de integridade", e));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }


    public TaskEntity saveOnDatabaseTask(Integer taskCode, String taskDescription) {
        try {
            TaskEntity taskEntity = new TaskEntity(taskCode, taskDescription);
            return taskRepository.save(taskEntity);
        } catch (DataIntegrityViolationException e) {
            return taskRepository.findByTaskCodeAndTaskDescription(taskCode, taskDescription)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao recuperar tarefa após falha de integridade", e));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível salvar os dados", e);
        }
    }

    public TagEntity saveOnDatabaseTags(TaskEntity taskCode, ProjectEntity projectCode, String tagName, Integer quant) {
        try {
            Optional<TagEntity> existing = tagRepository.findByTaskCodeAndProjectCodeAndTagNameAndQuant(
                taskCode, projectCode, tagName, quant
            );
    
            if (existing.isPresent()) {
                return existing.get();
            }
    
            TagEntity tagEntity = new TagEntity(taskCode, projectCode, tagName, quant);
            return tagRepository.save(tagEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }

    public StatusEntity saveOnDatabaseStats(Integer statusCode, String statusName) {
        try {
            StatusEntity statusEntity = new StatusEntity(statusCode, statusName);
            return statsRepository.save(statusEntity);
        } catch (DataIntegrityViolationException e) {
            return statsRepository.findByStatusCodeAndStatusName(statusCode, statusName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao recuperar status após falha de integridade", e));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }

    public TaskStatusHistoryEntity saveOnDatabaseTaskStatusHistory(TaskEntity taskCode, String lastStatus, String actualStatus, Timestamp changeDate) {
        try {            
            Optional<TaskStatusHistoryEntity> existing = taskStatusHistoryRepository.findByTaskCodeAndLastStatusAndActualStatusAndChangeDate(
                taskCode, lastStatus, actualStatus, changeDate
            );
    
            if (existing.isPresent()) {
                return existing.get();
            }
    
            TaskStatusHistoryEntity entity = new TaskStatusHistoryEntity(taskCode, lastStatus, actualStatus, changeDate);
            return taskStatusHistoryRepository.save(entity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }

    public UserTaskEntity saveOnDatabaseUserTask(TaskEntity taskCode, ProjectEntity projectCode, UserEntity userCode, MilestoneEntity milestoneCode, StatusEntity statsCode, RoleEntity roleCode, Timestamp startDate, Timestamp endDate, Integer quant) {
        try {
            Optional<UserTaskEntity> existing = userTaskRepository.findByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndStatsCodeAndRoleCodeAndStartDateAndEndDate(
                taskCode, projectCode, userCode, milestoneCode, statsCode, roleCode, startDate, endDate
            );
    
            if (existing.isPresent()) {
                return existing.get();
            }
    
            UserTaskEntity userTaskEntity = new UserTaskEntity(taskCode, projectCode, userCode, milestoneCode, statsCode, roleCode, startDate, endDate, quant);
            return userTaskRepository.save(userTaskEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }
}
