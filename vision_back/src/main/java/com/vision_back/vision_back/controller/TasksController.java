package com.vision_back.vision_back.controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
import com.vision_back.vision_back.configuration.TokenConfiguration;
import com.vision_back.vision_back.entity.PeriodEntity;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.RoleEntity;
import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.entity.dto.MilestoneDto;
import com.vision_back.vision_back.entity.dto.StatsDto;
import com.vision_back.vision_back.entity.dto.TagDto;
import com.vision_back.vision_back.repository.MilestoneRepository;
import com.vision_back.vision_back.repository.StatusRepository;
import com.vision_back.vision_back.repository.TagRepository;
import com.vision_back.vision_back.repository.TaskRepository;
import com.vision_back.vision_back.repository.TaskStatusHistoryRepository;
import com.vision_back.vision_back.service.AuthenticationService;
import com.vision_back.vision_back.service.AuthenticationServiceImpl;
import com.vision_back.vision_back.service.ProjectServiceImpl;
import com.vision_back.vision_back.service.TaskService;
import com.vision_back.vision_back.service.TaskServiceImpl;

import io.github.cdimascio.dotenv.Dotenv;

@RestController
@CrossOrigin
@RequestMapping("/tasks")
public class TasksController {

    @Autowired
    private TokenConfiguration tokenDto;

    @Autowired
    private TaskService tServ;

    @Autowired
    private StatusRepository sRepo;

    @Autowired
    private TagRepository tRepo;

    @Autowired
    private MilestoneRepository mRepo;

    @Autowired
    private ProjectServiceImpl psImpl;
    
    @Autowired
    private TaskStatusHistoryRepository tshImpl;

    @GetMapping("/count-tasks-by-status")
    public ResponseEntity<Map<String, Long>> countUserStoriesByStatus()  {
        tServ.processTasksAndStatsAndMilestone();
        List<StatsDto> statsList = sRepo.countTasksByStatus();
        Map<String, Long> tasksByStatus = statsList.stream().collect(Collectors.toMap(StatsDto::getStatusName, StatsDto::getQuant));
        return ResponseEntity.ok(tasksByStatus);
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

    @GetMapping("/tasks-per-sprint")
   public ResponseEntity<Map<String, Long>> getTasksPerSprint() {
        tServ.processTasksAndStatsAndMilestone();
        List<MilestoneDto> tasksSprint = mRepo.countCardsPerSprint();
        Map<String, Long> tasksPerSprint = tasksSprint.stream()
        .sorted(Comparator.comparing(m -> {
            String name = m.getMilestoneName();
            return Integer.parseInt(name.replaceAll("[^0-9]", ""));
        }))
        .collect(Collectors.toMap(
            MilestoneDto::getMilestoneName,
            MilestoneDto::getQuant,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));

        return ResponseEntity.ok(tasksPerSprint);   

    } 


    @GetMapping("/count-tasks-by-tag")
    public ResponseEntity<Map<String, Long>> countTasksByTag() {
        tServ.countTasksByTag();
        List<TagDto> statsList = tRepo.countTasksByTag();
        Map<String, Long> tasksByTag = statsList.stream().collect(Collectors.toMap(TagDto::getTagName, TagDto::getQuant));
        return ResponseEntity.ok(tasksByTag);
    }

    @GetMapping("/count-cards-by-status-closed")
    public ResponseEntity<Map<String, Long>> countTasksByStatusClosed() {
        tServ.processTasksAndStatsAndMilestone();
        List<StatsDto> statsList = sRepo.countTasksByStatusClosed();
        Map<String, Long> tasksByStatusClosed = statsList.stream().collect(Collectors.toMap(StatsDto::getStatusName, StatsDto::getQuant));
        return ResponseEntity.ok(tasksByStatusClosed);
    }
}