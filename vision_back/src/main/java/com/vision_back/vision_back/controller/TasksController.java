package com.vision_back.vision_back.controller;

import java.util.Comparator;
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
import com.vision_back.vision_back.repository.UserTaskRepository;
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

    
    @Autowired
    private UserTaskRepository ustRepo;

    @Operation(summary = "Conta as tarefas por status do usuário", description = "Conta o número de tarefas por status, baseado no ID do projeto e do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas por status retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-tasks-by-status")
    public ResponseEntity<Map<String, Long>> countUserStoriesByStatus() {
        for (String access : uRepo.accessControl()) {
            if (access.equals("STAKEHOLDER")) {
                List<StatsDto> statsList = sRepo.countTasksByStatusManager();
                Map<String, Long> tasksByStatus = statsList.stream()
                        .collect(Collectors.toMap(StatsDto::getStatusName, StatsDto::getQuant));
                return ResponseEntity.ok(tasksByStatus);
            }
        }
        List<StatsDto> statsList = sRepo.countTasksByStatusOperator();
        Map<String, Long> tasksByStatus = statsList.stream()
                .collect(Collectors.toMap(StatsDto::getStatusName, StatsDto::getQuant));
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
    public ResponseEntity<Map<String, Long>> getTasksPerSprint() {
        for (String access : uRepo.accessControl()) {
            if (access.equals("STAKEHOLDER")) {
                List<MilestoneDto> tasksSprint = mRepo.countCardsPerSprintManager();
                Map<String, Long> tasksPerSprint = tasksSprint.stream()
                        .sorted(Comparator.comparing(m -> {
                            String name = m.getMilestoneName();
                            return Integer.parseInt(name.replaceAll("[^0-9]", ""));
                        }))
                        .collect(Collectors.toMap(
                                MilestoneDto::getMilestoneName,
                                MilestoneDto::getQuant,
                                (e1, e2) -> e1,
                                LinkedHashMap::new));

                return ResponseEntity.ok(tasksPerSprint);
            }
        }
        List<MilestoneDto> tasksSprint = mRepo.countCardsPerSprintOperator();
        Map<String, Long> tasksPerSprint = tasksSprint.stream()
                .sorted(Comparator.comparing(m -> {
                    String name = m.getMilestoneName();
                    return Integer.parseInt(name.replaceAll("[^0-9]", ""));
                }))
                .collect(Collectors.toMap(
                        MilestoneDto::getMilestoneName,
                        MilestoneDto::getQuant,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        return ResponseEntity.ok(tasksPerSprint);
    }

    @Operation(summary = "Conta os retrabalhos", description = "Conta o número de retrabalhos, diferenciando entre gestores e operadores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contagem de tarefas retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a contagem de tarefas")
    })
    @GetMapping("/count-rework")
    public ResponseEntity<Map<String, Long>> countRework() {
        tServ.processRework();
        for (String access : uRepo.accessControl()) {
            if (access.equals("STAKEHOLDER")) {
                List<TaskStatusHistoryDto> rework = tshRepo.findTaskStatusHistoryWithReworkFlagManager();
                long totalRework = rework.stream().mapToLong(TaskStatusHistoryDto::getRework).sum();
                List<TaskDto> tasksDone = taskRepo.countTasksDoneManager();
                long totalDone = tasksDone.stream().mapToLong(TaskDto::getQuant).sum();
                return ResponseEntity.ok(Map.of("Concluidas", totalDone, "Retrabalho", totalRework));
            }
        }
        List<TaskStatusHistoryDto> rework = tshRepo.findTaskStatusHistoryWithReworkFlagOperator();
        long totalRework = rework.stream().mapToLong(TaskStatusHistoryDto::getRework).sum();
        List<TaskDto> tasksDone = taskRepo.countTasksDoneOperator();
        long totalDone = tasksDone.stream().mapToLong(TaskDto::getQuant).sum();
        return ResponseEntity.ok(Map.of("Concluidas", totalDone, "Retrabalho", totalRework));
    }

    @Operation(summary = "Conta as tarefas por tag do usuário", description = "Conta o número de tarefas de um usuário com base na tag associada, no projeto especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas por tag retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-tasks-by-tag")
    public ResponseEntity<Map<String, Long>> countTasksByTag() {
        tServ.countTasksByTag();
        for (String access : uRepo.accessControl()) {
            if (access.equals("STAKEHOLDER")) {
                List<TagDto> statsList = tagRepo.countTasksByTagManager();
                Map<String, Long> tasksByTag = statsList.stream()
                        .collect(Collectors.toMap(TagDto::getTagName, TagDto::getQuant));
                return ResponseEntity.ok(tasksByTag);
            }
        }
        List<TagDto> statsList = tagRepo.countTasksByTagOperator();
        Map<String, Long> tasksByTag = statsList.stream()
                .collect(Collectors.toMap(TagDto::getTagName, TagDto::getQuant));
        return ResponseEntity.ok(tasksByTag);
    }

    @Operation(summary = "Conta as tarefas fechadas do usuário por status", description = "Conta as tarefas fechadas de um usuário em um projeto, com base no status de cada sprint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas fechadas retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-cards-by-status-closed")
    public ResponseEntity<Map<String, Long>> countTasksByStatusClosed() {
        for (String access : uRepo.accessControl()) {
            if (access.equals("STAKEHOLDER")) {
                List<MilestoneDto> tasksSprint = mRepo.countCardsClosedPerSprintManager();
                Map<String, Long> tasksPerSprint = tasksSprint.stream()
                        .sorted(Comparator.comparing(m -> {
                            String name = m.getMilestoneName();
                            return Integer.parseInt(name.replaceAll("[^0-9]", ""));
                        }))
                        .collect(Collectors.toMap(
                                MilestoneDto::getMilestoneName,
                                MilestoneDto::getQuant,
                                (e1, e2) -> e1,
                                LinkedHashMap::new));

                return ResponseEntity.ok(tasksPerSprint);
            }
        }
        List<MilestoneDto> tasksSprint = mRepo.countCardsClosedPerSprintOperator();
        Map<String, Long> tasksPerSprint = tasksSprint.stream()
                .sorted(Comparator.comparing(m -> {
                    String name = m.getMilestoneName();
                    return Integer.parseInt(name.replaceAll("[^0-9]", ""));
                }))
                .collect(Collectors.toMap(
                        MilestoneDto::getMilestoneName,
                        MilestoneDto::getQuant,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        return ResponseEntity.ok(tasksPerSprint);
    }
    @GetMapping("/average-task-time-per-sprint")
    public ResponseEntity<Map<String, Long>> getAverageTaskTimePerSprint(){
        List<MilestoneDto> averageTime = mRepo.averageTaskTimePerSprint();
        Map<String, Long> averageTaskTimePerSprint = averageTime.stream()
        .sorted(Comparator.comparing(m -> {
            String name = m.getMilestoneName();
            return Integer.parseInt(name.replaceAll("[^0-9]", ""));
        }))
        .collect(Collectors.toMap(
            MilestoneDto::getMilestoneName,
            MilestoneDto::getQuant,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));

        return ResponseEntity.ok(averageTaskTimePerSprint);
    }
}