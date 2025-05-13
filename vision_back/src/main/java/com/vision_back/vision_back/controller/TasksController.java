package com.vision_back.vision_back.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vision_back.vision_back.entity.dto.MilestoneDto;
import com.vision_back.vision_back.entity.dto.ReworkDto;
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
import com.vision_back.vision_back.service.ProjectService;
import com.vision_back.vision_back.service.TaskService;
import com.vision_back.vision_back.service.UserService;

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
    private ProjectService pServ;

    @Autowired
    private TaskService tServ;

    @Autowired
    private UserService uServ;

    @Autowired
    private StatusRepository sRepo;

    @Autowired
    private UserRepository uRepo;

    @Autowired
    private TagRepository tagRepo;

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private UserTaskRepository userTaskRepo;

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
    public ResponseEntity<List<StatsDto>> countTasksByStatus(
            @RequestParam(required = false) String milestone,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String user) {
                    
        List<String> accessList = uRepo.accessControl();
        List<StatsDto> statsList;  
    
        if (accessList.contains("STAKEHOLDER")) {
            statsList = sRepo.countTasksByStatusManager(milestone, project, user);
        } else {
            statsList = sRepo.countTasksByStatusOperator(milestone, project, user);
        }
    
        return ResponseEntity.ok(statsList);
    }

    @Operation(summary = "Obtém as tarefas por sprint do usuário", description = "Retorna o número de tarefas de um usuário por sprint no projeto especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas por sprint retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/tasks-per-sprint")
    public ResponseEntity<List<MilestoneDto>> getTasksPerSprint(
            @RequestParam(required = false) String milestone,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String user) {

        List<String> accessList = uRepo.accessControl();
        List<MilestoneDto> tasksSprint;
        if (accessList.contains("STAKEHOLDER")) {
            tasksSprint = mRepo.countCardsPerSprintManager(milestone, project, user);
        } else if(accessList.contains("UX") ||
                  accessList.contains("BACK") ||
                  accessList.contains("FRONT") ||
                  accessList.contains("DESIGN")){
            tasksSprint = mRepo.countCardsPerSprintOperator(milestone, project, user);
        }else{
           tasksSprint = taskRepo.countTaskscreatedAdmin();
        }

        return ResponseEntity.ok(tasksSprint);
    }

    @GetMapping("/sync-all-process")
    public ResponseEntity<Void> syncAll() {
        try {
            pServ.processProject();
            pServ.processRoles();
            uServ.processAllUsers();
            tServ.processStatus();
            tServ.processMilestone();
            tServ.processTasks(false);
            tServ.baseProcessTaskUser();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Conta os retrabalhos", description = "Conta o número de retrabalhos, diferenciando entre gestores e operadores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contagem de tarefas retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a contagem de tarefas")
    })
    @GetMapping("/count-rework")
    public ResponseEntity<List<TaskStatusHistoryDto>> countRework(        
            @RequestParam(required = false) String milestone,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String user) {

            List<String> accessList = uRepo.accessControl();
            tServ.processTasks(true);
            
            List<TaskStatusHistoryDto> reworkDetails;
            
            if (accessList.contains("STAKEHOLDER")) {
                reworkDetails = tshRepo.findTaskStatusHistoryWithReworkFlagManager(milestone, project, user);
            } else {
                reworkDetails = tshRepo.findTaskStatusHistoryWithReworkFlagOperator(milestone, project, user);
            }
            
            return ResponseEntity.ok(reworkDetails);
        }

    @Operation(summary = "Conta as tarefas por tag do usuário", description = "Conta o número de tarefas de um usuário com base na tag associada, no projeto especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas por tag retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-tasks-by-tag")
    public ResponseEntity<List<TagDto>> countTasksByTag(
            @RequestParam(required = false) String milestone,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String user) {
                
        tServ.processTags();
    
        List<String> accessList = uRepo.accessControl();
        List<TagDto> statsList;
    
        if (accessList.contains("STAKEHOLDER")) {
            statsList = tagRepo.countTasksByTagManager(milestone, project, user);
        } else {
            statsList = tagRepo.countTasksByTagOperator(milestone, project, user);
        }
    
        return ResponseEntity.ok(statsList);
    }

    @Operation(summary = "Conta as tarefas fechadas do usuário por status", 
            description = "Conta as tarefas fechadas de um usuário em um projeto, com base no status de cada sprint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas fechadas retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-cards-by-status-closed")
    public ResponseEntity<List<MilestoneDto>> countTasksByStatusClosed(            
            @RequestParam(required = false) String milestone,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String user) {

        List<String> accessList = uRepo.accessControl();
        List<MilestoneDto> tasksSprint;

        if (accessList.contains("STAKEHOLDER")) {
            tasksSprint = mRepo.countCardsClosedPerSprintManager(milestone, project, user);
        } else {
            tasksSprint = mRepo.countCardsClosedPerSprintOperator(milestone, project, user);
        }
        return ResponseEntity.ok(tasksSprint);
    }
}