package com.vision_back.vision_back.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import com.vision_back.vision_back.entity.MilestoneEntity;

@Service
public interface TaskService {
    public HttpEntity<Void> setHeadersTasks();

    public void saveOnDatabaseTask(Integer taskCode, String taskDescription);

    public int countCardsCreatedByDateRange(Integer userId, Integer projectId, String startDate, String endDate);

    public Integer countTasksByStatusClosed(Integer projectId, Integer userId, String startDate, String endDate);

    public void processRework();

    public void processTasksAndStats();

    public void processTasksAndStatsAndMilestone();

    public Map<String, Integer> countTasksByStatusClosedBySprint(Integer userId, Integer projectId);

    public void countTasksByTag();

    public void processTaskHistory(Integer taskCode, Integer projectCode, Integer milestoneCode);

    public void processTaskUser(Integer projectCode, Integer taskCode, Integer userCode, Integer milestoneCode, Integer statsCode, Integer roleCode);

}
