package com.vision_back.vision_back.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.vision_back.vision_back.component.EntityRetryUtils;
import com.vision_back.vision_back.entity.MilestoneEntity;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.RoleEntity;
import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.TagEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.TaskStatusHistoryEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.entity.UserTaskEntity;
import com.vision_back.vision_back.repository.MilestoneRepository;
import com.vision_back.vision_back.repository.ProjectRepository;
import com.vision_back.vision_back.repository.RoleRepository;
import com.vision_back.vision_back.repository.StatusRepository;
import com.vision_back.vision_back.repository.TagRepository;
import com.vision_back.vision_back.repository.TaskRepository;
import com.vision_back.vision_back.repository.TaskStatusHistoryRepository;
import com.vision_back.vision_back.repository.UserRepository;
import com.vision_back.vision_back.repository.UserTaskRepository;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ProjectServiceImpl projectServiceImpl;
    
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserService userService;

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

    @Autowired
    private AuthenticationService auth;

    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();

    @Override
    public HttpEntity<Void> setHeadersTasks() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(auth.getCachedToken());

        return new HttpEntity<>(headers);
    }

    @Transactional
    @Override
    public void processTaskUser(JsonNode taskNode,
                                TaskEntity taskEntity,
                                ProjectEntity projectEntity,
                                UserEntity userEntity,
                                MilestoneEntity milestoneEntity,
                                StatusEntity statusEntity,
                                RoleEntity roleEntity) {

        try {
            Timestamp createdDate = Timestamp.from(Instant.parse(taskNode.get("created_date").asText()));
            JsonNode finishedDateNode = taskNode.get("finished_date");
            Timestamp finishedDate = (finishedDateNode != null && !finishedDateNode.isNull() && !finishedDateNode.asText().isEmpty())
                    ? Timestamp.from(Instant.parse(finishedDateNode.asText()))
                    : null;

            boolean exists = userTaskRepository.existsByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndStatsCodeAndRoleCodeAndStartDateAndEndDate(
                    taskEntity, projectEntity, userEntity, milestoneEntity, statusEntity, roleEntity, createdDate, finishedDate
            );

            if (!exists) {
                UserTaskEntity entity = new UserTaskEntity(
                        taskEntity, projectEntity, userEntity, milestoneEntity, statusEntity,
                        roleEntity, createdDate, finishedDate, 1
                );
                userTaskRepository.save(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @Override
    public void processTaskHistory(Integer taskCode, Integer projectCode, Integer milestoneCode, Integer userCode) {
        HttpEntity<Void> headersEntity = setHeadersTasks();

        ResponseEntity<String> responseTaskHistory = restTemplate.exchange(
                "https://api.taiga.io/api/v1/history/task/" + taskCode, HttpMethod.GET, headersEntity, String.class);
    
        try {
            JsonNode rootNodeTaskHistory = objectMapper.readTree(responseTaskHistory.getBody());
            List<TaskStatusHistoryEntity> taskHistoryEntities = new ArrayList<>();
    
            for (int i = rootNodeTaskHistory.size() - 1; i >= 0; i--) {
                JsonNode current = rootNodeTaskHistory.get(i);
    
                if (current.has("values_diff") && current.get("values_diff").has("status")
                        && !current.get("values_diff").get("status").isNull()) {
                    String statusAtual = current.get("values_diff").get("status").get(1).asText();
                    String ultimoStatus = current.get("values_diff").get("status").get(0).asText();
    
                    TaskEntity taskOpt =                     
                    EntityRetryUtils.retryUntilFound(
                        () -> taskRepository.findByTaskCode(taskCode).orElse(null),
                        5,
                        200,
                        "TaskEntity"
                    );

                    MilestoneEntity milestoneOpt = 
                    EntityRetryUtils.retryUntilFound(
                        () -> milestoneRepository.findByMilestoneCode(milestoneCode).orElse(null),
                        5,
                        200,
                        "MilestoneEntity"
                    );
                    
                    UserEntity userOpt =
                    EntityRetryUtils.retryUntilFound(
                        () -> userRepository.findByUserCode(userCode).orElse(null),
                        5,
                        200,
                        "UserEntity"
                    );

                    ProjectEntity projectOpt =
                    EntityRetryUtils.retryUntilFound(
                        () -> projectRepository.findByProjectCode(projectCode).orElse(null),
                        5,
                        200,
                        "UserEntity"
                    );

                    if (!taskStatusHistoryRepository.existsByTaskCodeAndProjectCodeAndMilestoneCodeAndUserCodeAndLastStatusAndActualStatusAndChangeDate(taskOpt, projectOpt, milestoneOpt, userOpt, ultimoStatus, statusAtual, Timestamp.from(Instant.parse(current.get("created_at").asText())))) {
                        taskHistoryEntities.add(new TaskStatusHistoryEntity(
                            taskOpt,
                            projectOpt,
                            milestoneOpt,
                            userOpt,
                            ultimoStatus,
                            statusAtual,
                            Timestamp.from(Instant.parse(current.get("created_at").asText()))
                        ));
                    }
                }    
            }
            taskStatusHistoryRepository.saveAll(taskHistoryEntities);   
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar histórico das tarefas", e);
        }
    }

    @Transactional
    @Override
    public void processRework() {
        try {
            HttpEntity<Void> headersEntity = setHeadersTasks();

            Integer projectCode = projectServiceImpl.getProjectId();

            String taskUrl = "https://api.taiga.io/api/v1/tasks?"
                    + "project=" + projectCode;
            ResponseEntity<String> taskResponse = restTemplate.exchange(taskUrl, HttpMethod.GET, headersEntity,
                    String.class);
            JsonNode tasks = objectMapper.readTree(taskResponse.getBody());

            for (JsonNode task : tasks) {
                Integer taskCode = task.get("id").asInt();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar tasks por sprint", e);
        }
    }

    @Override
    public Set<Integer> getMilestoneCodes() {
        HttpEntity<Void> headersEntity = setHeadersTasks();

        Integer projectCode = projectServiceImpl.getProjectId();
        Set<Integer> listMilestoneCode = new HashSet<>();

        String sprintUrl = "https://api.taiga.io/api/v1/milestones?project=" + projectCode;
        ResponseEntity<String> sprintResponse = restTemplate.exchange(sprintUrl, HttpMethod.GET, headersEntity,
                String.class);

        try {
            JsonNode sprints = objectMapper.readTree(sprintResponse.getBody());

            for (JsonNode sprint : sprints) {
                listMilestoneCode.add(sprint.get("id").asInt());
            }
            return listMilestoneCode;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar tasks por sprint", e);
        }
    }

    @Transactional
    @Override
    public void baseProcessTaskUser() {
        HttpEntity<Void> headersEntity = setHeadersTasks();
    
        Integer userCode = userServiceImpl.getUserId();
        Integer projectCode = projectServiceImpl.getProjectId();
        Integer roleCode = projectServiceImpl.getSpecificProjectUserRoleId();
    
        try {
            String taskUrl = "https://api.taiga.io/api/v1/tasks?&project=" + projectCode;
            ResponseEntity<String> taskResponse = restTemplate.exchange(taskUrl, HttpMethod.GET, headersEntity, String.class);
            JsonNode tasks = objectMapper.readTree(taskResponse.getBody());
    
            // Pré-buscas estáticas (que não mudam entre tarefas)
            Optional<ProjectEntity> projectOpt = EntityRetryUtils.retryUntilFound(
                () -> projectRepository.findByProjectCode(projectCode),
                5, 200, "ProjectEntity"
            );
            Optional<UserEntity> userOpt = EntityRetryUtils.retryUntilFound(
                () -> userRepository.findByUserCode(userCode),
                5, 200, "UserEntity"
            );
            Optional<RoleEntity> roleOpt = EntityRetryUtils.retryUntilFound(
                () -> roleRepository.findByRoleCode(roleCode),
                5, 200, "RoleEntity"
            );
    
            // Cache local para evitar reconsultas de milestones e status
            Map<Integer, MilestoneEntity> milestoneCache = new HashMap<>();
            Map<Integer, StatusEntity> statusCache = new HashMap<>();
    
            for (JsonNode task : tasks) {
                if (!task.has("milestone") || task.get("milestone").isNull()) continue;
    
                Integer sprintCode = task.get("milestone").asInt();
                Integer taskCode = task.get("id").asInt();
                Integer statusCode = task.get("status").asInt();
    
                // Milestone com cache
                MilestoneEntity milestone = milestoneCache.computeIfAbsent(sprintCode, code ->
                    EntityRetryUtils.retryUntilFound(
                        () -> milestoneRepository.findByMilestoneCode(code),
                        5, 200, "MilestoneEntity"
                    ).orElse(null)
                );
                if (milestone == null) continue;
    
                // Status com cache
                StatusEntity status = statusCache.computeIfAbsent(statusCode, code ->
                    EntityRetryUtils.retryUntilFound(
                        () -> statsRepository.findByStatusCode(code),
                        5, 200, "StatsEntity"
                    ).orElse(null)
                );
                if (status == null) continue;
    
                // Tarefa
                Optional<TaskEntity> taskOpt = EntityRetryUtils.retryUntilFound(
                    () -> taskRepository.findByTaskCode(taskCode),
                    5, 200, "TaskEntity"
                );
                if (taskOpt.isEmpty()) continue;
    
                boolean alreadyExists = userTaskRepository.existsByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndStatsCodeAndRoleCode(
                    taskOpt.get(), 
                    projectOpt.get(), 
                    userOpt.get(), 
                    milestone, 
                    status, 
                    roleOpt.get()
                );
    
                if (!alreadyExists) {
                    processTaskUser(task, taskOpt.get(), projectOpt.get(), userOpt.get(), milestone, status, roleOpt.get());
                }
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar tasks por sprint", e);
        }
    }
    
    @Transactional
    @Override
    public void processMilestone() {
        HttpEntity<Void> headersEntity = setHeadersTasks();

        Integer projectCode = projectServiceImpl.getProjectId();

        String sprintUrl = "https://api.taiga.io/api/v1/milestones?project=" + projectCode;
        ResponseEntity<String> sprintResponse = restTemplate.exchange(sprintUrl, HttpMethod.GET, headersEntity,
                String.class);

        try {
            JsonNode sprints = objectMapper.readTree(sprintResponse.getBody());
            Set<Integer> processedMilestoneCodes = new HashSet<>();
            List<MilestoneEntity> milestoneEntities = new ArrayList<>();

            for (JsonNode sprint : sprints) {

                String sprintName = sprint.get("name").asText();
                String startDate = sprint.get("estimated_start").asText();
                String endDate = sprint.get("estimated_finish").asText();
                
                if (!processedMilestoneCodes.contains(sprint.get("id").asInt()) && !milestoneRepository.existsByMilestoneCode(sprint.get("id").asInt())) {
                    milestoneEntities.add(new MilestoneEntity(
                        sprint.get("id").asInt(), 
                        sprintName, 
                        LocalDate.parse(startDate), 
                        LocalDate.parse(endDate),             
                        EntityRetryUtils.retryUntilFound(
                        () -> projectRepository.findByProjectCode(projectCode).orElse(null),
                        5,
                        200,
                        "ProjectEntity"
                    )));
                }
            }    
            milestoneRepository.saveAll(milestoneEntities);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar tasks por sprint", e);
        }
    }

    @Transactional
    @Override
    public void processTasks(boolean processHistory) {
        Integer projectCode = projectServiceImpl.getProjectId();
        Integer userCode = userService.getUserId();
    
        HttpEntity<Void> headersEntity = setHeadersTasks();
    
        String tasksUrl = "https://api.taiga.io/api/v1/tasks?"
                        + "project=" + projectCode;
    
        ResponseEntity<String> sprintResponse = restTemplate.exchange(tasksUrl, HttpMethod.GET, headersEntity, String.class);
    
        try {
            JsonNode tasks = objectMapper.readTree(sprintResponse.getBody());
            Set<Integer> processedTaskCodes = new HashSet<>();
            List<TaskEntity> taskEntities = new ArrayList<>();
    
            for (JsonNode node : tasks) {
                Integer taskCode = node.get("id").asInt();
                String subject = node.get("subject").asText();

                System.out.println("Task Code: " + taskCode + ", Subject: " + subject);
    
                MilestoneEntity milestone;
                    milestone = EntityRetryUtils.retryUntilFound(
                        () -> milestoneRepository.findByMilestoneCode(node.get("milestone").asInt()).orElse(null),
                        5,
                        200,
                        "MilestoneEntity"
                    );
    
                if (!taskRepository.existsByTaskCode(taskCode)) {
                    taskRepository.save(new TaskEntity(taskCode, subject, milestone));
                }

                if (processHistory) {
                    processTaskHistory(taskCode, projectCode, node.get("milestone").asInt(), node.get("assigned_to").asInt());
                }
            }
    

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @Override
    public void processStatus() {
        Integer projectCode = projectServiceImpl.getProjectId();

        HttpEntity<Void> headersEntity = setHeadersTasks();

        String tasksUrl = "https://api.taiga.io/api/v1/tasks?project=" + projectCode;

        ResponseEntity<String> response = restTemplate.exchange(tasksUrl, HttpMethod.GET, headersEntity, String.class);

        try {
            JsonNode tasks = objectMapper.readTree(response.getBody());
            Set<Integer> existingCodes = statsRepository.findAll()
                    .stream()
                    .map(StatusEntity::getStatusCode)
                    .collect(Collectors.toSet());

            Map<Integer, String> newStatusMap = new HashMap<>();

            for (JsonNode node : tasks) {
                Integer code = node.get("status").asInt();
                String name = node.get("status_extra_info").get("name").asText();

                if (!existingCodes.contains(code) && !newStatusMap.containsKey(code)) {
                    newStatusMap.put(code, name);
                }
            }

            List<StatusEntity> toSave = newStatusMap.entrySet().stream()
                    .map(e -> new StatusEntity(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());

            statsRepository.saveAll(toSave);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar tasks por sprint", e);
        }
    }

    @Transactional
    @Override
    public void processTags() {
        HttpEntity<Void> headersEntity = setHeadersTasks();
        Integer projectCode = projectServiceImpl.getProjectId();
        Integer userCode = userServiceImpl.getUserId();
        List<TagEntity> tagEntities = new ArrayList<>();
    
        String sprintUrl = "https://api.taiga.io/api/v1/milestones?project=" + projectCode;
        ResponseEntity<String> sprintResponse = restTemplate.exchange(sprintUrl, HttpMethod.GET, headersEntity,
                String.class);
    
        try {
            JsonNode sprints = objectMapper.readTree(sprintResponse.getBody());
    
    
            for (JsonNode sprint : sprints) {
                Integer sprintCode = sprint.get("id").asInt();
    
                ResponseEntity<String> response = restTemplate.exchange(
                        "https://api.taiga.io/api/v1/tasks?project=" + projectCode + "&assigned_to=" + userCode,
                        HttpMethod.GET,
                        headersEntity,
                        String.class);
    
                JsonNode rootNode = objectMapper.readTree(response.getBody());
    
                for (JsonNode node : rootNode) {
                    
                    Integer taskCode = node.get("id").asInt();
                    Integer taskSprintCode = node.get("milestone").asInt(); 
    
                    if (taskSprintCode.equals(sprintCode)) {
                        for (JsonNode tagNode : node.get("tags")) {
                            for (JsonNode tag : tagNode) {
                                if (!tag.isNull()) {
                                    String tagName = tag.asText();
                                    if (!tagRepository.existsByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndTagName(
                                        EntityRetryUtils.retryUntilFound(
                                            () -> taskRepository.findByTaskCode(taskCode).orElse(null),
                                            5,
                                            200,
                                            "TaskEntity"
                                        ), 
                                        EntityRetryUtils.retryUntilFound(
                                            () -> projectRepository.findByProjectCode(projectCode).orElse(null),
                                            5,
                                            200,
                                            "ProjectEntity"
                                        ),
                                        EntityRetryUtils.retryUntilFound(
                                            () -> userRepository.findByUserCode(userCode).orElse(null),
                                            5,
                                            200,
                                            "UserEntity"
                                        ), 
                                        EntityRetryUtils.retryUntilFound(
                                            () -> milestoneRepository.findByMilestoneCode(taskSprintCode).orElse(null),
                                            5,
                                            200,
                                            "MilestoneEntity"
                                        ),
                                        tagName)) {
                                        tagEntities.add(
                                            new TagEntity(
                                                EntityRetryUtils.retryUntilFound(
                                                    () -> taskRepository.findByTaskCode(taskCode).orElse(null),
                                                    5,
                                                    200,
                                                    "TaskEntity"
                                                ), 
                                                EntityRetryUtils.retryUntilFound(
                                                    () -> projectRepository.findByProjectCode(projectCode).orElse(null),
                                                    5,
                                                    200,
                                                    "ProjectEntity"
                                                ),
                                                EntityRetryUtils.retryUntilFound(
                                                    () -> userRepository.findByUserCode(userCode).orElse(null),
                                                    5,
                                                    200,
                                                    "UserEntity"
                                                ), 
                                                EntityRetryUtils.retryUntilFound(
                                                    () -> milestoneRepository.findByMilestoneCode(taskSprintCode).orElse(null),
                                                    5,
                                                    200,
                                                    "MilestoneEntity"
                                                ), 
                                                tagName,
                                                1));
                                    }
                                }
                            }
                        }
                    }
                }
                tagRepository.saveAll(tagEntities);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao processar as User Stories", e);
        }
    }

    // @Transactional
    // public MilestoneEntity saveOnDatabaseMilestone(Integer milestoneCode, String milestoneName, LocalDate estimatedStart,
    //         LocalDate estimatedEnd, Integer projectCode) {
    //     if (!milestoneRepository.existsByMilestoneCodeAndMilestoneNameAndEstimatedStartAndEstimatedEnd(milestoneCode,
    //             milestoneName, estimatedStart, estimatedEnd)) {
    //         MilestoneEntity milestoneEntity = new MilestoneEntity(milestoneCode, milestoneName, estimatedStart,
    //                 estimatedEnd, projectCode);
    //         return milestoneRepository.save(milestoneEntity);
    //     }
    //     return null;
    // }


    // @Transactional
    // public TaskEntity saveOnDatabaseTask(Integer taskCode, String taskDescription) {
    //     if (!taskRepository.existsByTaskCodeAndTaskDescription(taskCode, taskDescription)) {
    //         TaskEntity taskEntity = new TaskEntity(taskCode, taskDescription);
    //         return taskRepository.save(taskEntity);
    //     }
    //     return null;
    // }


    // @Transactional
    // public TagEntity saveOnDatabaseTags(Integer taskCode, Integer projectCode, Integer userCode, String tagName, Integer quant) {
    //     if (!tagRepository.existsByTaskCodeAndProjectCodeAndUserCodeAndTagNameAndQuant(taskCode, projectCode, userCode, tagName, quant)) {
    //         TagEntity tagEntity = new TagEntity(taskCode, projectCode, userCode, tagName, quant);
    //         return tagRepository.save(tagEntity);
    //     }
    //     return null;
    // }


    // @Transactional
    // public StatusEntity saveOnDatabaseStats(Integer statusCode, String statusName) {
    //     if (!statsRepository.existsByStatusCodeAndStatusName(statusCode, statusName)) {
    //         StatusEntity statusEntity = new StatusEntity(statusCode, statusName);
    //         return statsRepository.save(statusEntity);
    //     }
    //     return null;
    // }

    // @Transactional
    // public TaskStatusHistoryEntity saveOnDatabaseTaskStatusHistory(Integer taskCode, Integer userCode, String lastStatus, String actualStatus,
    //         Timestamp changeDate) {
    //     if (!taskStatusHistoryRepository.existsByTaskCodeAndLastStatusAndActualStatusAndChangeDate(taskCode, lastStatus, actualStatus, changeDate)) {
    //         TaskStatusHistoryEntity entity = new TaskStatusHistoryEntity(taskCode, userCode, lastStatus, actualStatus,
    //                 changeDate);
    //         return taskStatusHistoryRepository.save(entity);
    //     }
    //     return null;
    // }

    // @Transactional
    // public UserTaskEntity saveOnDatabaseUserTask(Integer taskCode, Integer projectCode, Integer userCode,
    //         Integer milestoneCode, Integer statsCode, Integer roleCode, Timestamp startDate,
    //         Timestamp endDate, Integer quant) {
    //     if (!userTaskRepository
    //             .existsByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndStatsCodeAndRoleCodeAndStartDateAndEndDate(
    //                     taskCode, projectCode, userCode, milestoneCode, statsCode, roleCode, startDate, endDate)) {
    //         UserTaskEntity userTaskEntity = new UserTaskEntity(taskCode, projectCode, userCode, milestoneCode,
    //                 statsCode, roleCode, startDate, endDate, quant);
    //         return userTaskRepository.save(userTaskEntity);
    //     }
    //     return null;
    // }
}