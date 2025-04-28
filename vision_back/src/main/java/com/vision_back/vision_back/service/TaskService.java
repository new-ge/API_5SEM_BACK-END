package com.vision_back.vision_back.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import com.vision_back.vision_back.entity.MilestoneEntity;
=======
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
>>>>>>> 7755d7d10b68a3537644f3e21afa2859565581e0

@Service
public interface TaskService {
    public HttpEntity<Void> setHeadersTasks();

    public void saveOnDatabaseTask(Integer taskCode, String taskDescription);

    public int countCardsCreatedByDateRange(Integer userId, Integer projectId, String startDate, String endDate);

    public void processRework();

    public void processTasksAndStats();

    public void processTasksAndStatsAndMilestone();

    public Map<String, Integer> countTasksByStatusClosedBySprint(Integer userId, Integer projectId);

    public void countTasksByTag();


    public void processTaskHistory(Integer taskCode, Integer projectCode, Integer milestoneCode);

    public List<Map<String, Object>> getUsersAndTasksPerSprintName() throws JsonMappingException, JsonProcessingException;

    public void processTaskUser(Integer projectCode, Integer taskCode, Integer userCode, Integer milestoneCode, Integer statsCode, Integer roleCode);

}
