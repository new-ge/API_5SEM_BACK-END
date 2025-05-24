package com.vision_back.vision_back.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import com.vision_back.vision_back.entity.dto.StatsDto;
import com.vision_back.vision_back.entity.dto.TagDto;
import com.vision_back.vision_back.entity.dto.TaskStatusHistoryDto;
import com.vision_back.vision_back.entity.dto.UserTaskAverageDTO;
import com.vision_back.vision_back.repository.MilestoneRepository;
import com.vision_back.vision_back.repository.ProjectRepository;
import com.vision_back.vision_back.repository.RoleRepository;
import com.vision_back.vision_back.repository.StatusRepository;
import com.vision_back.vision_back.repository.TagRepository;
import com.vision_back.vision_back.repository.TaskRepository;
import com.vision_back.vision_back.repository.TaskStatusHistoryRepository;
import com.vision_back.vision_back.repository.UserRepository;
import com.vision_back.vision_back.repository.UserTaskRepository;
import com.vision_back.vision_back.service.ProjectService;
import com.vision_back.vision_back.service.TaskService;
import com.vision_back.vision_back.service.UserProjectHelperService;
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
    private ProjectRepository pRepo;

    @Autowired
    private RoleRepository rRepo;

    @Autowired
    private TaskStatusHistoryRepository taskHistoryRepo;

    @Autowired
    private UserProjectHelperService userProjectService;

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
            statsList = sRepo.countTasksByStatusManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
        } else if(accessList.contains("UX") ||
                  accessList.contains("BACK") ||
                  accessList.contains("FRONT") ||
                  accessList.contains("DESIGN")){
            statsList = sRepo.countTasksByStatusOperator(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), userProjectService.fetchLoggedUserName());
        }else{
            statsList = sRepo.countTasksByStatusAdmin(milestone, project, user);
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
            tasksSprint = mRepo.countCardsPerSprintManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
        } else if (accessList.contains("UX") ||
                  accessList.contains("BACK") ||
                  accessList.contains("FRONT") ||
                  accessList.contains("DESIGN")) {
            tasksSprint = mRepo.countCardsPerSprintOperator(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), userProjectService.fetchLoggedUserName());
        } else {
           tasksSprint = mRepo.countCardsPerSprintAdmin(milestone, project, user);
        }

        return ResponseEntity.ok(tasksSprint);
    }
    @Operation(summary = "Sincroniza todos os dados do sistema", description = "Retorna a sincronização dos dados de projetos, usuários, sprints, status e tarefas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sincronização realizada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição") 
        })

    @GetMapping("/sync-all-process")
    public ResponseEntity<Void> syncAll() {
        try {
            uServ.processAllUsers();
            if (mRepo.count() == 0 || sRepo.count() == 0 || taskRepo.count() == 0 || userTaskRepo.count() == 0 || uRepo.count() == 0 || pRepo.count() == 0 || rRepo.count() == 0 || taskHistoryRepo.count() == 0) {
                pServ.processProject();
                pServ.processRoles();
                tServ.processStatus();
                tServ.processMilestone();
                tServ.processTasks(true);
                tServ.baseProcessTaskUser();
            }
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
            
            List<TaskStatusHistoryDto> reworkDetails;
            
            if (accessList.contains("STAKEHOLDER")) {
                reworkDetails = tshRepo.findTaskStatusHistoryWithReworkFlagManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
            } else if(accessList.contains("UX") ||
                  accessList.contains("BACK") ||
                  accessList.contains("FRONT") ||
                  accessList.contains("DESIGN")) {
                reworkDetails = tshRepo.findTaskStatusHistoryWithReworkFlagOperator(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), userProjectService.fetchLoggedUserName());
            } else {
                reworkDetails = tshRepo.findTaskStatusHistoryWithReworkFlagAdmin(milestone, project, user);
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
    
        List<String> accessList = uRepo.accessControl();
        List<TagDto> statsList;

        if (tagRepo.count() == 0) {
            tServ.processTags();
        }
    
        if (accessList.contains("STAKEHOLDER")) {
            statsList = tagRepo.countTasksByTagManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
        } else if(accessList.contains("UX") ||
                  accessList.contains("BACK") ||
                  accessList.contains("FRONT") ||
                  accessList.contains("DESIGN")) {
            statsList = tagRepo.countTasksByTagOperator(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), userProjectService.fetchLoggedUserName());
        }else{
            statsList = tagRepo.countTasksByTagAdmin(milestone, project, user);
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
            tasksSprint = mRepo.countCardsClosedPerSprintManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
        } else if(accessList.contains("UX") ||
                  accessList.contains("BACK") ||
                  accessList.contains("FRONT") ||
                  accessList.contains("DESIGN")){
            tasksSprint = mRepo.countCardsClosedPerSprintOperator(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), userProjectService.fetchLoggedUserName());
        }else{
            tasksSprint = mRepo.countCardsClosedPerSprintAdmin(milestone, project, user);
        }
        return ResponseEntity.ok(tasksSprint);
    }

    
    @Operation(summary = "Tempo médio de execução dos cards por usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tempo médio calculado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })

    @GetMapping("/average-time")
    public ResponseEntity<List<UserTaskAverageDTO>> getAverageExecutionTime(
        @RequestParam(required = false) String milestone,
        @RequestParam(required = false) String project,
        @RequestParam(required = false) String user
    ) {
        try {
            List<String> accessList = uRepo.accessControl();
            List<UserTaskAverageDTO> result;

            if (accessList.contains("STAKEHOLDER")) {
                result = userTaskRepo.findAverageTimeByFiltersManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
            } else if(accessList.contains("UX") ||
                  accessList.contains("BACK") ||
                  accessList.contains("FRONT") ||
                  accessList.contains("DESIGN")){
                result = userTaskRepo.findAverageTimeByFiltersOperador(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), userProjectService.fetchLoggedUserName());
            }else{
                result = userTaskRepo.findAverageTimeByFiltersAdmin(milestone, project, user);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.emptyList());
        }
    }

    @Operation(summary = "Retorna as milestones (sprints) disponíveis para o operador", description = "Retorna os nomes das sprints que o operador pode acessar.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de sprints retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/sprints-for-operator")
    public ResponseEntity<List<MilestoneDto>> getSprintsForOperator(
        @RequestParam(required = false) String project,
        @RequestParam(required = false) String user) {
            
        try {
            List<String> accessList = uRepo.accessControl();

            System.out.println(accessList);

            if (accessList.contains("UX") || 
                accessList.contains("BACK") || 
                accessList.contains("FRONT") || 
                accessList.contains("DESIGN")) {

                List<MilestoneDto> milestones = mRepo
                    .countCardsPerSprintOperator(null, project, user)
                    .stream()
                    .map(milestone -> new MilestoneDto(milestone.getMilestoneName())) 
                    .distinct() 
                    .collect(Collectors.toList());

                return ResponseEntity.ok(milestones); 
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
        }
    }
    @Operation( summary = "Obtém as sprints de todos os operadores e gestores", description = "Retorna as informações de todas as sprints de todos os operadores e gestores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações de sprints retornadas com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    
    @GetMapping("/sprints-for-admin")
    public ResponseEntity<List<MilestoneDto>> getSprintsForAdmin(
            @RequestParam(required = false) String milestone,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String user) {

        List<String> accessList = uRepo.accessControl();
        List<MilestoneDto> milestones;

        try {
            if (accessList.contains("PRODUCT OWNER")) {
                milestones = taskRepo.countTaskscreatedAdmin(milestone, project, user); 
            } else if (accessList.contains("STAKEHOLDER")) {
                milestones = mRepo.countCardsPerSprintManager(milestone, project, user);
            } else if (accessList.contains("UX") || accessList.contains("BACK") ||
                    accessList.contains("FRONT") || accessList.contains("DESIGN")) {
                milestones = mRepo.countCardsPerSprintOperator(milestone, project, user);
            } else {
                milestones = new ArrayList<>(); 
            }

            return ResponseEntity.ok(milestones);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}