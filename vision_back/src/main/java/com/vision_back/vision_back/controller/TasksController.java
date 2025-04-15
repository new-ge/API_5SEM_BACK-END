package com.vision_back.vision_back.controller;

import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.vision_back.vision_back.entity.dto.TokenDto;
import com.vision_back.vision_back.repository.TaskStatusHistoryRepository;
import com.vision_back.vision_back.service.AuthenticationService;
import com.vision_back.vision_back.service.AuthenticationServiceImpl;
import com.vision_back.vision_back.service.ProjectServiceImpl;
import com.vision_back.vision_back.service.TaskServiceImpl;

import io.github.cdimascio.dotenv.Dotenv;

@RestController
@CrossOrigin
@RequestMapping("/tasks")
public class TasksController {

    @Autowired
    private TokenDto tokenDto;

    @Autowired
    private TaskServiceImpl tsImpl;

    @Autowired
    private ProjectServiceImpl psImpl;
    
    @Autowired
    private TaskStatusHistoryRepository tshImpl;

    @GetMapping("/count-tasks-by-status")
    public Map<String, Integer> countUserStoriesByStatus(String token) throws JsonMappingException, JsonProcessingException {
        token = tokenDto.getAuthToken();
        return tsImpl.countTasksById();
    }

    @GetMapping("/att")
    public List<Object[]> tarefas() {
        return tshImpl.findTaskStatusHistoryWithReworkFlagNative();
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


    @GetMapping("/count-tasks-by-tag")
    public Map<String, Integer> countTasksByTag(
        String token) 
        {
        token = tokenDto.getAuthToken();
        return tsImpl.countTasksByTag();
    }

    @GetMapping("/count-cards-by-status-closed/{userId}/{projectId}")
    public Map<String, Integer> countTasksByStatusClosed(
        @PathVariable Integer projectId, 
        @PathVariable Integer userId) {
            
        TaskServiceImpl taskService = new TaskServiceImpl();
        return taskService.countTasksByStatusClosedBySprint(userId, projectId);
    }
}