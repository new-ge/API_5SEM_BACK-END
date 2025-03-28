package com.vision_back.vision_back.service;

import java.util.Map;

import org.springframework.http.HttpEntity;

public interface TaskService {
    public HttpEntity<Void> setHeadersTasks();

    public Map<String, Integer> countTasksById(Integer projectId, Integer userId);
}
