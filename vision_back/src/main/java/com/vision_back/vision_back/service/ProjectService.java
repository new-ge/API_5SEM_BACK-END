package com.vision_back.vision_back.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import com.vision_back.vision_back.entity.ProjectEntity;

@Service
public interface ProjectService {
    
    public HttpEntity<Void> setHeadersProject();
    
    public String getProjectBySlug(String slugProject);

    public Integer getProjectId();

    public ProjectEntity saveOnDatabaseProject(Integer projectCode, String projectName);

    public Integer getSpecificProjectUserRoleId();
}
