package com.vision_back.vision_back.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
import jakarta.servlet.http.HttpServletResponse;

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

            if (accessList.contains("STAKEHOLDER")) {

                List<StatsDto> statsList = sRepo.countTasksByStatusManager(milestone, project, user);
                List<MilestoneDto> tasksSprint = mRepo.countCardsPerSprintManager(milestone, project, user);
                List<TaskStatusHistoryDto> reworkDetails = tshRepo.findTaskStatusHistoryWithReworkFlagManager(milestone, project, user);
                List<TagDto> tagList = tagRepo.countTasksByTagManager(milestone, project, user);
                List<MilestoneDto> tasksSprintClosed = mRepo.countCardsClosedPerSprintManager(milestone, project, user);

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
                response.setHeader("Content-Disposition", "attachment; filename=relatorio.xlsx");

                OutputStream outputStream = response.getOutputStream();
                workbook.write(outputStream);
                workbook.close();
                outputStream.close();

            } else {
                List<StatsDto> statsList = sRepo.countTasksByStatusOperator(milestone, project, user);
                List<MilestoneDto> tasksSprint = mRepo.countCardsPerSprintOperator(milestone, project, user);
                List<TaskStatusHistoryDto> reworkDetails = tshRepo.findTaskStatusHistoryWithReworkFlagOperator(milestone, project, user);
                List<TagDto> tagList = tagRepo.countTasksByTagOperator(milestone, project, user);
                List<MilestoneDto> tasksSprintClosed = mRepo.countCardsClosedPerSprintOperator(milestone, project, user);

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
                response.setHeader("Content-Disposition", "attachment; filename=relatorio.xlsx");

                OutputStream outputStream = response.getOutputStream();
                workbook.write(outputStream);
                workbook.close();
                outputStream.close();
            }
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
        } else {
            tasksSprint = mRepo.countCardsPerSprintOperator(milestone, project, user);
        }

        return ResponseEntity.ok(tasksSprint);
    }
    @Operation(summary = "Sincroniza todos os processos para popular o banco de dados", description = "Sincroniza os processos para evitar erros no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Processos sincronizados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao sincronizar os processos")
    })
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
