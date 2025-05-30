package com.vision_back.vision_back.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vision_back.vision_back.entity.MilestoneEntity;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.RoleEntity;
import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.UserEntity;

@Service
public interface TaskService {
    public HttpEntity<Void> setHeadersTasks();

    public void processRework();

    public void processMilestone();
    
    public List<Integer> processMilestoneList();

    public void processTasks(boolean processHistory);

    public List<Integer> processTasksList();

    public void processStatus();

    public List<Integer> processStatusList();

    public void processTags();

    public List<String> processTagsList();
    
    public Set<Integer> getMilestoneCodes();

    public void processTaskHistory(Integer taskCode, Integer projectCode, Integer milestoneCode, Integer userCode);

    public void processTaskUser(JsonNode taskNode,
                                    TaskEntity taskEntity,
                                    ProjectEntity projectEntity,
                                    UserEntity userEntity,
                                    MilestoneEntity milestoneEntity,
                                    StatusEntity statusEntity,
                                    RoleEntity roleEntity);

    public void baseProcessTaskUser();
}
