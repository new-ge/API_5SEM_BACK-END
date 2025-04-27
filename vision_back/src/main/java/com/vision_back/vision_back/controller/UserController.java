package com.vision_back.vision_back.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.vision_back.vision_back.service.TaskServiceImpl;
import com.vision_back.vision_back.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private TaskServiceImpl taskService;

    @GetMapping("users-and-tasks/project/{projectId}")
    public List<Map<String, Object>> getUsersAndTasks(@PathVariable Integer projectId) {
        return userService.getUsersAndTasks(projectId);
    }

    @GetMapping("users-and-tasks-per-sprint-name")
    public List<Map<String, Object>> getUsersAndTasksPerSprintName() throws JsonMappingException, JsonProcessingException {
        return taskService.getUsersAndTasksPerSprintName();
    }
    
}
