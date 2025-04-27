package com.vision_back.vision_back.controller;

<<<<<<< HEAD
import com.vision_back.vision_back.service.AuthenticationService;
import com.vision_back.vision_back.service.UserServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
=======
import com.vision_back.vision_back.service.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
>>>>>>> 71c7d93dd61ff73b11b8badc3d6f324d3a2423e3

import java.util.List;
import java.util.Map;

<<<<<<< HEAD
=======
@Tag(name = "Users", description = "Endpoints relacionados aos usuários")
>>>>>>> 71c7d93dd61ff73b11b8badc3d6f324d3a2423e3
@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

<<<<<<< HEAD
    @Autowired
    private AuthenticationService authenticationService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Rotas já existentes
=======
    @Operation(summary = "Lista usuários e tarefas por projeto", description = "Retorna uma lista de usuários e suas tarefas associadas a um projeto específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários e tarefas retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar usuários e tarefas")
    })
>>>>>>> 71c7d93dd61ff73b11b8badc3d6f324d3a2423e3
    @GetMapping("users-and-tasks/project/{projectId}")
    public List<Map<String, Object>> getUsersAndTasks(@PathVariable Integer projectId) {
        return userService.getUsersAndTasks(projectId);
    }

<<<<<<< HEAD
=======
    @Operation(summary = "Lista usuários e tarefas por projeto e sprint", description = "Retorna uma lista de usuários e suas tarefas associadas a um projeto e sprint específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários e tarefas retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Projeto ou Sprint não encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar usuários e tarefas")
    })
>>>>>>> 71c7d93dd61ff73b11b8badc3d6f324d3a2423e3
    @GetMapping("users-and-tasks-per-sprint-name/project/{projectId}/sprint/{sprintName}")
    public List<Map<String, Object>> getUsersAndTasksPerSprintName(
            @PathVariable Integer projectId,
            @PathVariable String sprintName) {
        return userService.getUsersAndTasksPerSprintName(projectId, sprintName);
    }
<<<<<<< HEAD
    

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        try {
            String role = authenticationService.authenticateAndGetRole(username, password);
            return role; // Retorna a role diretamente
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao autenticar: " + e.getMessage();
        }
    }
=======

>>>>>>> 71c7d93dd61ff73b11b8badc3d6f324d3a2423e3
}
