package com.vision_back.vision_back.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vision_back.vision_back.service.TaskServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("/tasks")
public class TasksController {
    @GetMapping("/count-tasks-by-status/{projectId}/{userId}")
    public Map<String, Integer> countUserStoriesByStatus(@PathVariable Integer projectId, @PathVariable Integer userId) {
        TaskServiceImpl tsImpl = new TaskServiceImpl();
        System.out.println(tsImpl.countTasksById(1641986, 758714));
        return tsImpl.countTasksById(projectId, userId);
    }
    
    @GetMapping("/count-cards-by-period/{userId}/{projectId}/{startDate}/{endDate}")
    public int countCardsByPeriod(
        @PathVariable Integer projectId, 
        @PathVariable Integer userId, 
        @PathVariable String startDate, 
        @PathVariable String endDate) {
            
        TaskServiceImpl taskService = new TaskServiceImpl();
        return taskService.countCardsCreatedByDateRange(userId, projectId, startDate, endDate);
    }
    
    @GetMapping("/tasks-per-sprint/{userId}/{projectId}")
    public Map<String, Integer> getTasksPerSprint(@PathVariable Integer projectId, @PathVariable Integer userId) {
        TaskServiceImpl taskService = new TaskServiceImpl();
        return taskService.getTasksPerSprint(userId,projectId);
    }
}
