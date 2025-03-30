package com.vision_back.vision_back.service;

import java.util.Map;

import org.springframework.http.HttpEntity;

public interface TaskService {
    public HttpEntity<Void> setHeadersTasks(Integer projectId, Integer userId);

    public Map<String, Integer> countTasksById(Integer projectId, Integer userId);

    public int countCardsCreatedByDateRange(Integer userId, Integer projectId, String startDate, String endDate);

    public Integer countTasksByStatusClosed(Integer projectId, Integer userId, String startDate, String endDate); 

<<<<<<< HEAD
    public Map<String, Integer> countTasksByTag(Integer TaskId, Integer TagId);
=======
    public Map<String, Integer> getTasksPerSprint(Integer userId, Integer projectId);

    public Map<String, Integer> countTasksByStatusClosedBySprint(Integer userId, Integer projectId);
>>>>>>> 876e2e11dc81d79cea5a84b6448771d26a1677c1
}
