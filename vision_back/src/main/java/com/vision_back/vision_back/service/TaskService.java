package com.vision_back.vision_back.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.TaskStatusHistoryEntity;
import com.vision_back.vision_back.entity.UserTaskEntity;

@Service
public interface TaskService {
    public HttpEntity<Void> setHeadersTasks();

    public TaskEntity saveOnDatabaseTask(Integer taskCode, String taskDescription);

    public int countCardsCreatedByDateRange(Integer userId, Integer projectId, String startDate, String endDate);

    public Integer countTasksByStatusClosed(Integer projectId, Integer userId, String startDate, String endDate);

    public void processTasksAndStats();

    public void processTasksAndStatsAndMilestone();

    // public Map<String, Integer> getTasksPerSprint(Integer userId, Integer projectId);

    public Map<String, Integer> countTasksByStatusClosedBySprint(Integer userId, Integer projectId);

    public Map<String, Integer> countTasksByTag();

    public void processTaskHistory(Integer taskCode);

    public void processTaskUser(Integer projectCode, Integer taskCode, Integer userCode, Integer milestoneCode, Integer statsCode, Integer roleCode);

    // public Map<String, Integer> getTasksPerSprint();
}
