package com.vision_back.vision_back.controller;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vision_back.vision_back.entity.dto.MilestoneDto;
import com.vision_back.vision_back.entity.dto.StatsDto;
import com.vision_back.vision_back.entity.dto.TagDto;
import com.vision_back.vision_back.entity.dto.TaskDto;
import com.vision_back.vision_back.entity.dto.TaskStatusHistoryDto;
import com.vision_back.vision_back.repository.MilestoneRepository;
import com.vision_back.vision_back.repository.StatusRepository;
import com.vision_back.vision_back.repository.TagRepository;
import com.vision_back.vision_back.repository.TaskRepository;
import com.vision_back.vision_back.repository.TaskStatusHistoryRepository;
import com.vision_back.vision_back.repository.UserRepository;
import com.vision_back.vision_back.service.TaskService;
import com.vision_back.vision_back.service.TaskServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "tasks", description = "Endpoints relacionados às tarefas")
@RestController
@CrossOrigin
@RequestMapping("/tasks")
public class TasksController {

    @Autowired
    private TaskService tServ;

    @Autowired
    private StatusRepository sRepo;

    @Autowired
    private UserRepository uRepo;

    @Autowired
    private TagRepository tagRepo;

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private TaskStatusHistoryRepository tshRepo;

    @Autowired
    private MilestoneRepository mRepo;
    
    @Operation(summary = "Conta as tarefas por status do usuário", description = "Conta o número de tarefas por status, baseado no ID do projeto e do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas por status retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-tasks-by-status")
    public ResponseEntity<Map<String, Map<String, Map<String, Map<String, Long>>>>> countTasksByStatus() {
        Map<String, Map<String, Map<String, Map<String, Long>>>> tasksByStatus = new HashMap<>();
        
        for (String access : uRepo.accessControl()) {
            List<StatsDto> statsList;
    
            if (access.equals("STAKEHOLDER")) {
                statsList = sRepo.countTasksByStatusManager();
            } else {
                statsList = sRepo.countTasksByStatusOperator();
            }
    
            for (StatsDto statsDto : statsList) {
                String projeto = statsDto.getMilestoneName();
                String usuario = statsDto.getUserName();
                String sprint = statsDto.getProjectName();
                String status = statsDto.getStatusName();
                Long quant = statsDto.getQuant();

                tasksByStatus
                    .computeIfAbsent(projeto, k -> new HashMap<>())
                    .computeIfAbsent(usuario, k -> new HashMap<>())
                    .computeIfAbsent(sprint, k -> new HashMap<>())
                    .merge(status, quant, Long::sum);
            }
        }
    
        return ResponseEntity.ok(tasksByStatus);
    }

    @Operation(summary = "Conta os tarefas criados por período", description = "Conta o número de tarefas criados dentro de um período especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-cards-by-period/{userId}/{projectId}/{startDate}/{endDate}")
    public int countCardsByPeriod(
            @PathVariable Integer projectId,
            @PathVariable Integer userId,
            @PathVariable String startDate,
            @PathVariable String endDate) {

        TaskServiceImpl taskService = new TaskServiceImpl();
        return taskService.countCardsCreatedByDateRange(userId, projectId, startDate, endDate);
    }

    
    @Operation(summary = "Obtém as tarefas por sprint do usuário", description = "Retorna o número de tarefas de um usuário por sprint no projeto especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas por sprint retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })

    @GetMapping("/tasks-per-sprint")
    public ResponseEntity<Map<String, Map<String, Map<String, Long>>>> getTasksPerSprint() {
        Map<String, Map<String, Map<String, Long>>> tasksPerSprint = new HashMap<>();
        
        for (String access : uRepo.accessControl()) {
            List<MilestoneDto> tasksSprint;
        
            if (access.equals("STAKEHOLDER")) {
                tasksSprint = mRepo.countCardsPerSprintManager();
            } else {
                tasksSprint = mRepo.countCardsPerSprintOperator();
            }
        
            for (MilestoneDto milestoneDto : tasksSprint) {
                String usuario = milestoneDto.getUserName(); 
                String sprint = milestoneDto.getMilestoneName();
                String projeto = milestoneDto.getProjectName();
        
                tasksPerSprint
                    .computeIfAbsent(usuario, k -> new HashMap<>())
                    .computeIfAbsent(projeto, k -> new HashMap<>())
                    .merge(sprint, milestoneDto.getQuant(), Long::sum);
            }
        }
    
        return ResponseEntity.ok(tasksPerSprint);
    }


    @GetMapping("/count-rework")
    public ResponseEntity<Map<String, Map<String, Map<String, Map<String, Long>>>>> countRework() {
        tServ.processRework();
    
        for (String access : uRepo.accessControl()) {
            List<TaskStatusHistoryDto> rework;
            List<TaskDto> tasksDone;

            if (access.equals("STAKEHOLDER")) {
                rework = tshRepo.findTaskStatusHistoryWithReworkFlagManager();
                tasksDone = taskRepo.countTasksDoneManager();
            } else {
                rework = tshRepo.findTaskStatusHistoryWithReworkFlagOperator();
                tasksDone = taskRepo.countTasksDoneOperator();
            }
    
            Map<String, Map<String, Map<String, Map<String, Long>>>> reworkData = new HashMap<>();
    
            for (TaskStatusHistoryDto taskHistory : rework) {
                String usuario = taskHistory.getUserName();
                String projeto = taskHistory.getProjectName();
                String sprint = taskHistory.getMilestoneName();
                Long reworkCount = taskHistory.getRework() != null ? taskHistory.getRework() : 0L;
    
                reworkData
                    .computeIfAbsent(projeto, k -> new HashMap<>())
                    .computeIfAbsent(usuario, k -> new HashMap<>())
                    .computeIfAbsent(sprint, k -> new HashMap<>())
                    .merge("Retrabalho", reworkCount, Long::sum);
            }
    
            Map<String, Map<String, Map<String, Map<String, Long>>>> tasksDoneData = new HashMap<>();
    
            for (TaskDto task : tasksDone) {
                String usuario = task.getUserName();
                String projeto = task.getProjectName();
                String sprint = task.getMilestoneName();
                Long doneCount = task.getQuant() != null ? task.getQuant() : 0L;
    
                tasksDoneData
                    .computeIfAbsent(projeto, k -> new HashMap<>())
                    .computeIfAbsent(usuario, k -> new HashMap<>())
                    .computeIfAbsent(sprint, k -> new HashMap<>())
                    .merge("Concluidas", doneCount, Long::sum);
            }

            Map<String, Map<String, Map<String, Map<String, Long>>>> result = new HashMap<>();
            
            for (String projeto : reworkData.keySet()) {
                for (String usuario : reworkData.get(projeto).keySet()) {
                    for (String sprint : reworkData.get(projeto).get(usuario).keySet()) {
                        Long retrabalho = reworkData.get(projeto).get(usuario).get(sprint).get("Retrabalho");
                        Long concluido = tasksDoneData.getOrDefault(projeto, new HashMap<>())
                                                      .getOrDefault(usuario, new HashMap<>())
                                                      .getOrDefault(sprint, new HashMap<>())
                                                      .getOrDefault("Concluidas", 0L);
                        
                        Map<String, Long> sprintData = new HashMap<>();
                        sprintData.put("Concluidas", concluido);
                        sprintData.put("Retrabalho", retrabalho);
    
                        result
                            .computeIfAbsent(projeto, k -> new HashMap<>())
                            .computeIfAbsent(usuario, k -> new HashMap<>())
                            .put(sprint, sprintData);
                    }
                }
            }
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.ok(new HashMap<>());
    }    

    @Operation(summary = "Conta as tarefas por tag do usuário", description = "Conta o número de tarefas de um usuário com base na tag associada, no projeto especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas por tag retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-tasks-by-tag")
    public ResponseEntity<Map<String, Map<String, Map<String, Long>>>> countTasksByTag() {
        for (String access : uRepo.accessControl()) {
            List<TagDto> statsList;

            if (access.equals("STAKEHOLDER")) {
                statsList = tagRepo.countTasksByTagManager();
            } else {
                statsList = tagRepo.countTasksByTagOperator();
            }

            Map<String, Map<String, Map<String, Long>>> tasksByTag = new HashMap<>();

            for (TagDto tagDto : statsList) {
                String projeto = tagDto.getProjectName();
                String usuario = tagDto.getUserName();
                String sprint = tagDto.getMilestoneName();

                tasksByTag
                    .computeIfAbsent(projeto, k -> new HashMap<>())
                    .computeIfAbsent(usuario, k -> new HashMap<>())
                    .merge(sprint, tagDto.getQuant(), Long::sum);

            }

            return ResponseEntity.ok(tasksByTag);
        }
        return ResponseEntity.ok(new HashMap<>());
    }

    @Operation(summary = "Conta as tarefas fechadas do usuário por status", description = "Conta as tarefas fechadas de um usuário em um projeto, com base no status de cada sprint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas fechadas retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-cards-by-status-closed")
    public ResponseEntity<Map<String, Map<String, Map<String, Long>>>> countTasksByStatusClosed() {
        for (String access : uRepo.accessControl()) {
            List<MilestoneDto> tasksSprint;

            if (access.equals("STAKEHOLDER")) {
                tasksSprint = mRepo.countCardsClosedPerSprintManager();
            } else {
                tasksSprint = mRepo.countCardsClosedPerSprintOperator();
            }

            Map<String, Map<String, Map<String, Long>>> tasksByStatus = new HashMap<>();

            for (MilestoneDto milestone : tasksSprint) {
                String projeto = milestone.getProjectName();
                String usuario = milestone.getUserName(); 
                String sprint = milestone.getMilestoneName();  
                Long quant = milestone.getQuant();     

                tasksByStatus
                    .computeIfAbsent(usuario, k -> new HashMap<>())
                    .computeIfAbsent(projeto, k -> new HashMap<>())
                    .merge(sprint, quant, Long::sum);
            }

            return ResponseEntity.ok(tasksByStatus);
        }

        return ResponseEntity.ok(new HashMap<>());
    }

    @GetMapping("/average-task-time-per-sprint")
    public ResponseEntity<Map<String, Map<String, Map<String, Long>>>> getAverageTaskTimePerSprint() {
        for (String access : uRepo.accessControl()) {
            List<MilestoneDto> averageTime;
    
            if (access.equals("STAKEHOLDER")) {
                averageTime = mRepo.averageTaskTimePerSprintManager();
            } else {
                averageTime = mRepo.averageTaskTimePerSprintOperator();
            }
    
            Map<String, Map<String, Map<String, Long>>> averageTaskTimeBySprint = new HashMap<>();
    
            for (MilestoneDto milestone : averageTime) {
                String usuario = milestone.getUserName();
                String projeto = milestone.getProjectName();
                String sprint = milestone.getMilestoneName();
                Long quant = milestone.getQuant(); 
    
                averageTaskTimeBySprint
                    .computeIfAbsent(usuario, k -> new HashMap<>())
                    .computeIfAbsent(projeto, k -> new HashMap<>())
                    .merge(sprint, quant, Long::sum);
            }
    

            return ResponseEntity.ok(averageTaskTimeBySprint);
        }
    
        return ResponseEntity.ok(new HashMap<>());
    }    
}