package com.vision_back.vision_back.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import com.vision_back.vision_back.entity.TaskEntity;

@Service
public interface TaskService {
    public HttpEntity<Void> setHeadersTasks();

    public TaskEntity saveOnDatabaseTask(Integer taskCode, String taskDescription);

    public int countCardsCreatedByDateRange(Integer userId, Integer projectId, String startDate, String endDate);

    public Integer countTasksByStatusClosed(Integer projectId, Integer userId, String startDate, String endDate);

    public Map<String, Integer> countTasksById();

    public Map<String, Integer> getTasksPerSprint(Integer userId, Integer projectId);

    public Map<String, Integer> countTasksByStatusClosedBySprint(Integer userId, Integer projectId);

    public Map<Integer, Map<String, Integer>> countTasksByTag();
}
