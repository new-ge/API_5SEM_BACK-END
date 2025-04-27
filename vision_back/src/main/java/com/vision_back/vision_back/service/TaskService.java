package com.vision_back.vision_back.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public interface TaskService {
    public HttpEntity<Void> setHeadersTasks();

    public void saveOnDatabaseTask(Integer taskCode, String taskDescription);

    public int countCardsCreatedByDateRange(Integer userId, Integer projectId, String startDate, String endDate);

    public void processRework();

    public void processTasksAndStats();

    public void processTasksAndStatsAndMilestone();

    public Map<String, Integer> countTasksByStatusClosedBySprint(Integer userId, Integer projectId);

    public Map<String, Integer> countTasksByTag();

public List<Map<String, Object>> getUsersAndTasksPerSprintName() throws JsonMappingException, JsonProcessingException;

    public void processTaskHistory(Integer taskCode);

    public void processTaskUser(Integer projectCode, Integer taskCode, Integer userCode, Integer milestoneCode, Integer statsCode, Integer roleCode);

}
