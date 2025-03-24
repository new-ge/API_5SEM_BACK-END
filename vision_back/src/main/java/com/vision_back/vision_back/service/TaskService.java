package com.vision_back.vision_back.service;

import java.util.Map;

import org.springframework.http.HttpEntity;

public interface TaskService {
    public HttpEntity<Void> setHeadersTasks(String projectId, String userId);

    public Map<String, Integer> countTasksById(String projectId, String userId);
}
