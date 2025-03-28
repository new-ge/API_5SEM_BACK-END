package com.vision_back.vision_back.controller;


import com.vision_back.vision_back.service.TaskClosedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskClosedController {
    @Autowired
    private TaskClosedService taskService;

    @GetMapping("/closed/{projectId}/{userId}")
    public Long getClosedTasks(@PathVariable Integer projectId, @PathVariable Integer userId) {
        return taskService.getClosedTasksByUserAndProject(userId, projectId);
    }
}
