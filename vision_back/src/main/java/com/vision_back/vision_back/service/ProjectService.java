package com.vision_back.vision_back.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.RoleEntity;

@Service
public interface ProjectService {
    
    public HttpEntity<Void> setHeadersProject();
    
    public String getProjectBySlug(String slugProject);

    public Integer getProjectId();

    public ProjectEntity saveOnDatabaseProject(Integer projectCode, String projectName);

    public String getSpecificProjectUserRole();
}
