package com.vision_back.vision_back.controller;

import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.vision_back.vision_back.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "projects", description = "Endpoints relacionados aos projetos")
@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Operation(summary = "Lista todos os projetos do usuário", description = "Retorna todos os projetos associados a um usuário pelo código de usuário informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de projetos retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar os projetos")
    })
    @GetMapping("/get-all-projects/{userCode}")
    public List<TreeMap<String, Object>> getAllProjects(@PathVariable Integer userCode) {
        return projectService.listAllProjectsByUser(userCode);
    }

}
