package com.vision_back.vision_back.service;

import java.util.List;
import java.util.TreeMap;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;


import com.vision_back.vision_back.entity.ProjectEntity;

@Service
public interface ProjectService {
    
    public HttpEntity<Void> setHeadersProject();
    
    public String getProjectBySlug(String slugProject);

    public Integer getProjectId();

    public List<TreeMap<String, Object>> listAllProjectsByUser(Integer userCode);
    public void saveOnDatabaseProject(Integer projectCode, String projectName);

    public Integer getSpecificProjectUserRoleId();
}
