package com.vision_back.vision_back.service;

import java.util.List;

import org.springframework.http.HttpEntity;

import com.vision_back.vision_back.entity.dto.ProjectDto;

public interface ProjectService {
    
    public HttpEntity<Void> setHeadersProject();
    
    public String getProjectBySlug(String slugProject);

    public String getProjectId(Integer memberId);

    public List<ProjectDto> listAllProjects();
    
}
