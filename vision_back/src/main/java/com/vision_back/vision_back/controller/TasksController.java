package com.vision_back.vision_back.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "Conta as tarefas por status do usuário", description = "Conta o número de tarefas por status, baseado no ID do projeto e do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas por status retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-tasks-by-status/{projectId}/{userId}")
    public Map<String, Integer> countUserStoriesByStatus(@PathVariable Integer projectId,
            @PathVariable Integer userId) {
        TaskServiceImpl tsImpl = new TaskServiceImpl();
        return tsImpl.countTasksById(projectId, userId);
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
    @GetMapping("/tasks-per-sprint/{userId}/{projectId}")
    public Map<String, Integer> getTasksPerSprint(@PathVariable Integer projectId, @PathVariable Integer userId) {
        TaskServiceImpl taskService = new TaskServiceImpl();
        return taskService.getTasksPerSprint(userId, projectId);
    }

    @Operation(summary = "Conta as tarefas por tag do usuário", description = "Conta o número de tarefas de um usuário com base na tag associada, no projeto especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas por tag retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-tasks-by-tag/{projectId}/{userId}")
    public Map<String, Integer> countTasksByTag(
            @PathVariable Integer projectId,
            @PathVariable Integer userId) {

        TaskServiceImpl tsImpl = new TaskServiceImpl();
        System.out.println(tsImpl.countTasksByTag(projectId, userId));
        return tsImpl.countTasksByTag(projectId, userId);
    }

    @Operation(summary = "Conta as tarefas fechadas do usuário por status", description = "Conta as tarefas fechadas de um usuário em um projeto, com base no status de cada sprint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de tarefas fechadas retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar a requisição")
    })
    @GetMapping("/count-cards-by-status-closed/{userId}/{projectId}")
    public Map<String, Integer> countTasksByStatusClosed(
            @PathVariable Integer projectId,
            @PathVariable Integer userId) {

        TaskServiceImpl taskService = new TaskServiceImpl();
        return taskService.countTasksByStatusClosedBySprint(userId, projectId);
    }
}