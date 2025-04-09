package com.vision_back.vision_back.service;

import org.springframework.http.HttpEntity;

import com.vision_back.vision_back.entity.ProjectEntity;

public interface ProjectService {
    
    public HttpEntity<Void> setHeadersProject();
    
    public String getProjectBySlug(String slugProject);

    public Integer getProjectId(Integer memberId);
    
    public String getProjectName(Integer memberId);

    public ProjectEntity saveOnDatabase(Integer projectCode, String projectName);
}
