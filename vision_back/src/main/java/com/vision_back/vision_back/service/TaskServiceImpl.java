package com.vision_back.vision_back.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
import com.vision_back.vision_back.configuration.TokenConfiguration;
import com.vision_back.vision_back.entity.MilestoneEntity;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.RoleEntity;
import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.TaskStatusHistoryEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.entity.UserTaskEntity;
import com.vision_back.vision_back.entity.TagEntity;
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
    public void processTaskUser(Integer projectCode, Integer taskCode, Integer userCode, Integer milestoneCode,
            Integer statsCode, Integer roleCode) {
        HttpEntity<Void> headersEntity = setHeadersTasks();

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.taiga.io/api/v1/tasks?project=" + projectCode + "&assigned_to=" + userCode, HttpMethod.GET,
                headersEntity, String.class);
        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            ProjectEntity projectEntity = projectRepository.findByProjectCode(projectCode)
                    .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
            TaskEntity taskEntity = taskRepository.findByTaskCode(taskCode)
                    .orElseThrow(() -> new IllegalArgumentException("Task não encontrada"));
            UserEntity userEntity = userRepository.findByUserCode(userCode)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            StatusEntity statsEntity = statsRepository.findByStatusCode(statsCode)
                    .orElseThrow(() -> new IllegalArgumentException("Status não encontrado"));
            MilestoneEntity milestoneEntity = milestoneRepository.findByMilestoneCode(milestoneCode)
                    .orElseThrow(() -> new IllegalArgumentException("Milestone não encontrado"));
            RoleEntity roleEntity = roleRepository.findByRoleCode(roleCode)
                    .orElseThrow(() -> new IllegalArgumentException("Role não encontrada"));

            for (JsonNode node : rootNode) {
                if (node.get("id").asInt() == taskCode) {
                    JsonNode finishedDateNode = node.get("finished_date");
                    Timestamp finishedDate = null;
                    Timestamp createdDate = Timestamp.from(Instant.parse(node.get("created_date").asText()));
                    if (finishedDateNode != null && !finishedDateNode.isNull()
                            && !finishedDateNode.asText().isEmpty()) {
                        finishedDate = Timestamp.from(Instant.parse(finishedDateNode.asText()));
                    }
                    saveOnDatabaseUserTask(taskEntity, projectEntity, userEntity, milestoneEntity, statsEntity,
                            roleEntity, createdDate, finishedDate, 1);
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar User Stories", e);
        }
    }

    @Override
    public void processTaskHistory(Integer taskCode) {
        HttpEntity<Void> headersEntity = setHeadersTasks();
        Integer userCode = userServiceImpl.getUserId();

        ResponseEntity<String> responseTaskHistory = restTemplate.exchange(
                "https://api.taiga.io/api/v1/history/task/" + taskCode, HttpMethod.GET, headersEntity, String.class);

        try {
            JsonNode rootNodeTaskHistory = objectMapper.readTree(responseTaskHistory.getBody());
            for (int i = rootNodeTaskHistory.size() - 1; i >= 0; i--) {
                JsonNode current = rootNodeTaskHistory.get(i);

                if (current.has("values_diff") && current.get("values_diff").has("status")
                        && !current.get("values_diff").get("status").isNull()) {
                    String statusAtual = current.get("values_diff").get("status").get(1).asText();
                    String ultimoStatus = current.get("values_diff").get("status").get(0).asText();

                    TaskEntity taskEntity = taskRepository.findByTaskCode(taskCode)
                            .orElseThrow(() -> new IllegalArgumentException("Task não encontrada"));
                    UserEntity userEntity = userRepository.findByUserCode(userCode)
                            .orElseThrow(() -> new IllegalArgumentException("Task não encontrada"));

                    saveOnDatabaseTaskStatusHistory(taskEntity, userEntity, ultimoStatus, statusAtual, Timestamp.from(Instant.parse(current.get("created_at").asText())));

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

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.taiga.io/api/v1/tasks?project=" + projectCode + "&assigned_to=" + userCode, HttpMethod.GET,
                headersEntity, String.class);

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
    public void processRework() {
        try {
            HttpEntity<Void> headersEntity = setHeadersTasks();

            Integer userCode = userServiceImpl.getUserId();
            Integer projectCode = projectServiceImpl.getProjectId();

            String taskUrl = "https://api.taiga.io/api/v1/tasks?"
                    + "assigned_to=" + userCode + "&"
                    + "project=" + projectCode;
            ResponseEntity<String> taskResponse = restTemplate.exchange(taskUrl, HttpMethod.GET, headersEntity,
                    String.class);
            JsonNode tasks = objectMapper.readTree(taskResponse.getBody());

            for (JsonNode task : tasks) {
                Integer taskCode = task.get("id").asInt();
                saveOnDatabaseTask(taskCode, task.get("subject").asText());
                processTaskHistory(taskCode);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar tasks por sprint", e);
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

        Integer userCode = userServiceImpl.getUserId();
        Integer projectCode = projectServiceImpl.getProjectId();
        Integer roleCode = projectServiceImpl.getSpecificProjectUserRoleId();

        String sprintUrl = "https://api.taiga.io/api/v1/milestones?project=" + projectCode;
        ResponseEntity<String> sprintResponse = restTemplate.exchange(sprintUrl, HttpMethod.GET, headersEntity,
                String.class);

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
                        projectRepository.findByProjectCode(projectCode)
                                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado")));

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

        String sprintUrl = "https://api.taiga.io/api/v1/milestones?project=" + projectId;

        ResponseEntity<String> sprintResponse = restTemplate.exchange(sprintUrl, HttpMethod.GET, headersEntity,
                String.class);
        try {
            JsonNode sprints = objectMapper.readTree(sprintResponse.getBody());

            for (JsonNode sprint : sprints) {
                int sumClosed = 0;

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
                    saveOnDatabaseStats(Integer.valueOf(node.get("status").asInt()),
                            node.get("status_extra_info").get("name").asText());
                    processTaskHistory(node.get("id").asInt());

                    if ((node.get("status_extra_info").get("name").asText()).equals("Closed")) {
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
                saveOnDatabaseStats(Integer.valueOf(node.get("status").asInt()),
                        node.get("status_extra_info").get("name").asText());

                if ((node.get("status_extra_info").get("name").asText()).equals("Closed")) {
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

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.taiga.io/api/v1/tasks?project=" + projectCode + "&assigned_to=" + userCode,
                HttpMethod.GET,
                headersEntity,
                String.class);

        Map<String, Integer> tagCount = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            for (JsonNode node : rootNode) {
                saveOnDatabaseTask(node.get("id").asInt(), node.get("subject").asText());
                for (JsonNode tagNode : node.get("tags")) {
                    for (JsonNode tag : tagNode) {
                        if (!tag.isNull()) {
                            tagCount.put(tag.toString().replace("\"", ""),
                                    tagCount.getOrDefault(tag.toString().replace("\"", ""), 0) + 1);
                            TaskEntity taskEntity = taskRepository.findByTaskCode(node.get("id").asInt())
                                    .orElseThrow(() -> new IllegalArgumentException("Task não encontrada"));
                            ProjectEntity projectEntity = projectRepository.findByProjectCode(projectCode)
                                    .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
                            UserEntity userEntity = userRepository.findByUserCode(userCode)
                                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
                            saveOnDatabaseTags(taskEntity, projectEntity, userEntity, tag.asText(), 1);
                        }
                    }
                }
            }
            return tagCount;
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar as User Stories", e);
        }
    }

    @Transactional
    public void saveOnDatabaseMilestone(Integer milestoneCode, String milestoneName, LocalDate estimatedStart,
            LocalDate estimatedEnd, ProjectEntity projectCode) {
        if (!milestoneRepository.existsByMilestoneCodeAndMilestoneNameAndEstimatedStartAndEstimatedEnd(milestoneCode,
                milestoneName, estimatedStart, estimatedEnd)) {
            MilestoneEntity milestoneEntity = new MilestoneEntity(milestoneCode, milestoneName, estimatedStart,
                    estimatedEnd, projectCode);
            milestoneRepository.save(milestoneEntity);
        }
    }

    @Transactional
    public void saveOnDatabaseTask(Integer taskCode, String taskDescription) {
        if (!taskRepository.existsByTaskCodeAndTaskDescription(taskCode, taskDescription)) {
            TaskEntity taskEntity = new TaskEntity(taskCode, taskDescription);
            taskRepository.save(taskEntity);
        }
    }

    @Transactional
    public void saveOnDatabaseTags(TaskEntity taskCode, ProjectEntity projectCode, UserEntity userCode, String tagName, Integer quant) {
        if (!tagRepository.existsByTaskCodeAndProjectCodeAndUserCodeAndTagNameAndQuant(taskCode, projectCode, userCode, tagName, quant)) {
            TagEntity tagEntity = new TagEntity(taskCode, projectCode, userCode, tagName, quant);
            tagRepository.save(tagEntity);
        }
    }

    @Transactional
    public void saveOnDatabaseStats(Integer statusCode, String statusName) {
        if (!statsRepository.existsByStatusCodeAndStatusName(statusCode, statusName)) {
            StatusEntity statusEntity = new StatusEntity(statusCode, statusName);
            statsRepository.save(statusEntity);
        }
    }

    @Transactional
    public void saveOnDatabaseTaskStatusHistory(TaskEntity taskCode, UserEntity userCode, String lastStatus, String actualStatus,
            Timestamp changeDate) {
        if (!taskStatusHistoryRepository.existsByTaskCodeAndUserCodeAndLastStatusAndActualStatusAndChangeDate(taskCode, userCode, lastStatus,
                actualStatus, changeDate)) {
            TaskStatusHistoryEntity entity = new TaskStatusHistoryEntity(taskCode, userCode, lastStatus, actualStatus,
                    changeDate);
            taskStatusHistoryRepository.save(entity);
        }
    }

    @Transactional
    public void saveOnDatabaseUserTask(TaskEntity taskCode, ProjectEntity projectCode, UserEntity userCode,
            MilestoneEntity milestoneCode, StatusEntity statsCode, RoleEntity roleCode, Timestamp startDate,
            Timestamp endDate, Integer quant) {
        if (!userTaskRepository
                .existsByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndStatsCodeAndRoleCodeAndStartDateAndEndDate(
                        taskCode, projectCode, userCode, milestoneCode, statsCode, roleCode, startDate, endDate)) {
            UserTaskEntity userTaskEntity = new UserTaskEntity(taskCode, projectCode, userCode, milestoneCode,
                    statsCode, roleCode, startDate, endDate, quant);
            userTaskRepository.save(userTaskEntity);
        }
    }
}