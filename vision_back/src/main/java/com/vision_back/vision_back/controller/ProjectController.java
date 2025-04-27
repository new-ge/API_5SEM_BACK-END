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

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/get-all-projects/{userCode}")
    public List<TreeMap<String,Object>> getAllProjects(@PathVariable Integer userCode) {
        System.out.println("Endpoint /projects chamado com userId: " + userCode);
        return projectService.listAllProjectsByUser(userCode);
    }
    
}
