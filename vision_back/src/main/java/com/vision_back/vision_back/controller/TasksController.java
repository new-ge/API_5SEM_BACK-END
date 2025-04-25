package com.vision_back.vision_back.controller;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vision_back.vision_back.configuration.TokenConfiguration;
import com.vision_back.vision_back.entity.dto.MilestoneDto;
import com.vision_back.vision_back.entity.dto.StatsDto;
import com.vision_back.vision_back.entity.dto.TagDto;
import com.vision_back.vision_back.entity.dto.TaskDto;
import com.vision_back.vision_back.entity.dto.TaskStatusHistoryDto;
import com.vision_back.vision_back.repository.MilestoneRepository;
import com.vision_back.vision_back.repository.StatusRepository;
import com.vision_back.vision_back.repository.TagRepository;
import com.vision_back.vision_back.repository.TaskRepository;
import com.vision_back.vision_back.repository.TaskStatusHistoryRepository;
import com.vision_back.vision_back.service.ProjectServiceImpl;
import com.vision_back.vision_back.service.TaskService;
import com.vision_back.vision_back.service.TaskServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("/tasks")
public class TasksController {

    @Autowired
    private TaskService tServ;

    @Autowired
    private StatusRepository sRepo;

    @Autowired
    private TagRepository tagRepo;

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private TaskStatusHistoryRepository tshRepo;

    @Autowired
    private MilestoneRepository mRepo;
    
    @GetMapping("/count-tasks-by-status")
    public ResponseEntity<Map<String, Long>> countUserStoriesByStatus()  {
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

    @GetMapping("/count-rework")
    public ResponseEntity<Map<String, Long>> countRework() {
        tServ.processRework();
        List<TaskStatusHistoryDto> rework = tshRepo.findTaskStatusHistoryWithReworkFlag();
        long totalRework = rework.stream().mapToLong(TaskStatusHistoryDto::getRework).sum();
        List<TaskDto> tasksDone = taskRepo.countTasksDone();
        long totalDone = tasksDone.stream().mapToLong(TaskDto::getQuant).sum();
        return ResponseEntity.ok(Map.of("Concluidas", totalDone, "Retrabalho", totalRework));
    }


    @GetMapping("/count-tasks-by-tag")
    public ResponseEntity<Map<String, Long>> countTasksByTag() {
        tServ.countTasksByTag();
        List<TagDto> statsList = tagRepo.countTasksByTag();
        Map<String, Long> tasksByTag = statsList.stream().collect(Collectors.toMap(TagDto::getTagName, TagDto::getQuant));
        return ResponseEntity.ok(tasksByTag);
    }

    @GetMapping("/count-cards-by-status-closed")
    public ResponseEntity<Map<String, Long>> countTasksByStatusClosed() {
        List<MilestoneDto> tasksSprint = mRepo.countCardsClosedPerSprint();
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
}