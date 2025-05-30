package com.vision_back.vision_back.controller;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.component.SyncUtils;
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
import com.vision_back.vision_back.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "tasks", description = "Endpoints relacionados às tarefas")
@RestController
@CrossOrigin
@RequestMapping("/tasks")
public class TasksController {

    private final TaskServiceImpl taskServiceImpl;
    
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
    private UserProjectHelperService userProjectService;

    @Autowired
    private UserTaskRepository userTaskRepo;

    @Autowired
    private TaskStatusHistoryRepository tshRepo;

    @Autowired
    private MilestoneRepository mRepo;  

    ObjectMapper objectMapper = new ObjectMapper();
    HttpHeaders headers = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> headersEntity;

    TasksController(TaskServiceImpl taskServiceImpl) {
        this.taskServiceImpl = taskServiceImpl;
    }
    
    @Operation(summary = "Exporta um Excel com todos os dados da aplicação", description = "Retorna um Excel com todos os dados, dependendo no nivel de acesso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Excel retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar o Excel")
    })
    @GetMapping("/request-excel")
    public ResponseEntity<Void> exportToExcel(HttpServletResponse response, 
                              @RequestParam(required = false) String milestone,
                              @RequestParam(required = false) String project,
                              @RequestParam(required = false) String user)  {
        try {
            List<String> accessList = uRepo.accessControl();
            Workbook workbook = new XSSFWorkbook();

            List<StatsDto> statsList;
            List<MilestoneDto> tasksSprint;
            List<TaskStatusHistoryDto> reworkDetails;
            List<TagDto> tagList;
            List<MilestoneDto> tasksSprintClosed;

            if (accessList.contains("Stakeholder")) {
                statsList = sRepo.countTasksByStatusManager(milestone, project, user);
                tasksSprint = mRepo.countCardsPerSprintManager(milestone, project, user);
                reworkDetails = tshRepo.findTaskStatusHistoryWithReworkFlagManager(milestone, project, user);
                tagList = tagRepo.countTasksByTagManager(milestone, project, user);
                tasksSprintClosed = mRepo.countCardsClosedPerSprintManager(milestone, project, user);
            } else if(accessList.contains("UX") ||
                      accessList.contains("Back") ||
                      accessList.contains("Front") ||
                      accessList.contains("Design")) {
                statsList = sRepo.countTasksByStatusOperator(milestone, project, user);
                tasksSprint = mRepo.countCardsPerSprintOperator(milestone, project, user);
                reworkDetails = tshRepo.findTaskStatusHistoryWithReworkFlagOperator(milestone, project, user);
                tagList = tagRepo.countTasksByTagOperator(milestone, project, user);
                tasksSprintClosed = mRepo.countCardsClosedPerSprintOperator(milestone, project, user);
            } else {
                statsList = sRepo.countTasksByStatusAdmin(milestone, project, user);
                tasksSprint = mRepo.countCardsPerSprintAdmin(milestone, project, user);
                reworkDetails = tshRepo.findTaskStatusHistoryWithReworkFlagAdmin(milestone, project, user);
                tagList = tagRepo.countTasksByTagAdmin(milestone, project, user);
                tasksSprintClosed = mRepo.countCardsClosedPerSprintAdmin(milestone, project, user);
            }

            Sheet statusSheet = workbook.createSheet("Tarefas por Status");

            Row headerStatusRow = statusSheet.createRow(0);
            headerStatusRow.createCell(0).setCellValue("Projeto");
            headerStatusRow.createCell(1).setCellValue("Operador");
            headerStatusRow.createCell(2).setCellValue("Sprint");
            headerStatusRow.createCell(3).setCellValue("Status");
            headerStatusRow.createCell(4).setCellValue("Qtd Tarefas");

            int rowIdxStatus = 1;
            for (StatsDto stats : statsList) {
                Row row = statusSheet.createRow(rowIdxStatus++);
                row.createCell(0).setCellValue(stats.getProjectName());
                row.createCell(1).setCellValue(stats.getUserName());
                row.createCell(2).setCellValue(stats.getMilestoneName());
                row.createCell(3).setCellValue(stats.getStatusName());
                row.createCell(4).setCellValue(stats.getQuant());
            }

            Sheet createdCardsSheet = workbook.createSheet("Tarefas Criadas");

            Row headerCreatedCardsRow = createdCardsSheet.createRow(0);
            headerCreatedCardsRow.createCell(0).setCellValue("Projeto");
            headerCreatedCardsRow.createCell(1).setCellValue("Operador");
            headerCreatedCardsRow.createCell(2).setCellValue("Sprint");
            headerCreatedCardsRow.createCell(3).setCellValue("Qtd Tarefas Criadas");

            int rowIdxCreatedCards = 1;
            for (MilestoneDto milestoneDto : tasksSprint) {
                Row row = createdCardsSheet.createRow(rowIdxCreatedCards++);
                row.createCell(0).setCellValue(milestoneDto.getProjectName());
                row.createCell(1).setCellValue(milestoneDto.getUserName());
                row.createCell(2).setCellValue(milestoneDto.getMilestoneName());
                row.createCell(3).setCellValue(milestoneDto.getQuant());
            }

            Sheet reworkSheet = workbook.createSheet("Retrabalhos");

            Row headerReworkSheet = reworkSheet.createRow(0);
            headerReworkSheet.createCell(0).setCellValue("Projeto");
            headerReworkSheet.createCell(1).setCellValue("Operador");
            headerReworkSheet.createCell(2).setCellValue("Sprint");
            headerReworkSheet.createCell(3).setCellValue("Qtd Retrabalhos");

            int rowIdxRework = 1;
            for (TaskStatusHistoryDto rework : reworkDetails) {
                Row row = reworkSheet.createRow(rowIdxRework++);
                row.createCell(0).setCellValue(rework.getProjectName());
                row.createCell(1).setCellValue(rework.getUserName());
                row.createCell(2).setCellValue(rework.getMilestoneName());
                row.createCell(3).setCellValue(rework.getRework());
            }

            Sheet tagSheet = workbook.createSheet("Tarefas por Tag");

            Row headerTagSheet = tagSheet.createRow(0);
            headerTagSheet.createCell(0).setCellValue("Projeto");
            headerTagSheet.createCell(1).setCellValue("Operador");
            headerTagSheet.createCell(2).setCellValue("Sprint");
            headerTagSheet.createCell(3).setCellValue("Tag");
            headerTagSheet.createCell(4).setCellValue("Qtd Tarefas por Tag");

            int rowIdxTag = 1;
            for (TagDto tag : tagList) {
                Row row = tagSheet.createRow(rowIdxTag++);
                row.createCell(0).setCellValue(tag.getProjectName());
                row.createCell(1).setCellValue(tag.getUserName());
                row.createCell(2).setCellValue(tag.getMilestoneName());
                row.createCell(3).setCellValue(tag.getTagName());
                row.createCell(4).setCellValue(tag.getQuant());
            }

            Sheet tasksClosedSheet = workbook.createSheet("Tarefas Finalizadas");

            Row headerTasksClosedSheet = tasksClosedSheet.createRow(0);
            headerTasksClosedSheet.createCell(0).setCellValue("Projeto");
            headerTasksClosedSheet.createCell(1).setCellValue("Operador");
            headerTasksClosedSheet.createCell(2).setCellValue("Sprint");
            headerTasksClosedSheet.createCell(3).setCellValue("Qtd Tarefas Finalizadas");

            int rowIdxTaskClosed = 1;
            for (MilestoneDto sprintClosed : tasksSprintClosed) {
                Row row = tasksClosedSheet.createRow(rowIdxTaskClosed++);
                row.createCell(0).setCellValue(sprintClosed.getProjectName());
                row.createCell(1).setCellValue(sprintClosed.getUserName());
                row.createCell(2).setCellValue(sprintClosed.getMilestoneName());
                row.createCell(3).setCellValue(sprintClosed.getQuant());
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment");

            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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
    
        if (accessList.contains("Stakeholder")) {
            statsList = sRepo.countTasksByStatusManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
            } else if(accessList.contains("UX") ||
                accessList.contains("Back") ||
                accessList.contains("Front") ||
                accessList.contains("Design")){
            statsList = sRepo.countTasksByStatusOperator(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), userProjectService.fetchLoggedUserName());
        } else {
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
        if (accessList.contains("Stakeholder")) {
            tasksSprint = mRepo.countCardsPerSprintManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
        } else if(accessList.contains("UX") ||
            accessList.contains("Back") ||
            accessList.contains("Front") ||
            accessList.contains("Design")){
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

            SyncUtils.processIfAnyMissing(
                pServ.processProjectList(),
                code -> pRepo.existsByProjectCode(code),
                pServ::processProject
            );
            
            SyncUtils.processIfAnyMissing(
                pServ.processRolesList(),
                code -> rRepo.existsByRoleCode(code),
                pServ::processRoles
            );
            
            SyncUtils.processIfAnyMissing(
                tServ.processStatusList(),
                code -> sRepo.existsByStatusCode(code),
                tServ::processStatus
            );
            
            SyncUtils.processIfAnyMissing(
                tServ.processMilestoneList(),
                code -> mRepo.existsByMilestoneCode(code),
                tServ::processMilestone
            );

            SyncUtils.processIfAnyMissing(
                tServ.processTasksList(),
                code -> taskRepo.existsByTaskCode(code),
                () -> tServ.processTasks(true)
            );

            tServ.baseProcessTaskUser();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
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
            
            if (accessList.contains("Stakeholder")) {
                reworkDetails = tshRepo.findTaskStatusHistoryWithReworkFlagManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
            } else if(accessList.contains("UX") ||
                accessList.contains("Back") ||
                accessList.contains("Front") ||
                accessList.contains("Design")){
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

        tServ.processTags();
    
        if (accessList.contains("Stakeholder")) {
            statsList = tagRepo.countTasksByTagManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
        } else if(accessList.contains("UX") ||
                  accessList.contains("Back") ||
                  accessList.contains("Front") ||
                  accessList.contains("Design")){
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

        if (accessList.contains("Stakeholder")) {
            tasksSprint = mRepo.countCardsClosedPerSprintManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
        } else if(accessList.contains("UX") ||
                  accessList.contains("Back") ||
                  accessList.contains("Front") ||
                  accessList.contains("Design")){
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

            if (accessList.contains("Stakeholder")) {
                result = userTaskRepo.findAverageTimeByFiltersManager(milestone, userProjectService.fetchProjectNameByUserId(userProjectService.loggedUserId()), user);
            } else if(accessList.contains("UX") ||
                accessList.contains("Back") ||
                accessList.contains("Front") ||
                accessList.contains("Design")){
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

            if(accessList.contains("UX") ||
                accessList.contains("Back") ||
                accessList.contains("Front") ||
                accessList.contains("Design")) {

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
            } else if (accessList.contains("Stakeholder")) {
                milestones = mRepo.countCardsPerSprintManager(milestone, project, user);
            } else if(accessList.contains("UX") ||
                accessList.contains("Back") ||
                accessList.contains("Front") ||
                accessList.contains("Design")) {
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
