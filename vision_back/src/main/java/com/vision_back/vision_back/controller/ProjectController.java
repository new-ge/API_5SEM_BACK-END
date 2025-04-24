package com.vision_back.vision_back.controller;

import java.util.List;
import java.util.TreeMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.vision_back.vision_back.service.ProjectService;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController (ProjectService projectService){
        this.projectService = projectService;
    }

    @GetMapping
    public List<TreeMap<String,Object>> getAllProjects(@RequestParam Integer userId) {
        System.out.println("Endpoint /projects chamado com userId: " + userId);
        return projectService.listAllProjectsByUser(userId);
    }
    
}
