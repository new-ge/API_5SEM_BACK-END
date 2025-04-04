package com.vision_back.vision_back.service;

import java.util.Map;

import org.springframework.http.HttpEntity;

public interface TaskService {
    public HttpEntity<Void> setHeadersTasks(Integer projectId, Integer userId);

    public Map<String, Integer> countTasksById(Integer projectId, Integer userId);

    public int countCardsCreatedByDateRange(Integer userId, Integer projectId, String startDate, String endDate);

    public Integer countTasksByStatusClosed(Integer projectId, Integer userId, String startDate, String endDate);

    public Map<String, Integer> getTasksPerSprint(Integer userId, Integer projectId);

    public Map<String, Integer> countTasksByStatusClosedBySprint(Integer userId, Integer projectId);

    public Map<String, Integer> countTasksByTag(Integer projectId, Integer userId);
}
