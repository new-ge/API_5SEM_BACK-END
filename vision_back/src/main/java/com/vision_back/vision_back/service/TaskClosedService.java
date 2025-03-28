package com.vision_back.vision_back.service;

import com.vision_back.vision_back.entity.dto.TaskClosedDto;
import com.vision_back.vision_back.repository.TaskClosedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskClosedService {
    @Autowired
    private TaskClosedRepository taskClosedRepository;

    public Long getClosedTasksByUserAndProject(Integer userId, Integer projectId) {
        return taskClosedRepository.countClosedTasksByUserAndProject(userId, projectId);
    }
}