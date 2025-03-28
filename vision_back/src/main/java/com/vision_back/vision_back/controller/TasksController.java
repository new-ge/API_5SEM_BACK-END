package com.vision_back.vision_back.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vision_back.vision_back.service.TaskServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("/tasks")
public class TasksController {
    @GetMapping("/count-tasks-by-status/{projectId}/{userId}")
    public Map<String, Integer> countUserStoriesByStatus(@PathVariable Integer projectId, @PathVariable Integer userId) {
        TaskServiceImpl tsImpl = new TaskServiceImpl();
        System.out.println(tsImpl.countTasksById(projectId, userId));
        return tsImpl.countTasksById(projectId, userId);
    }
}