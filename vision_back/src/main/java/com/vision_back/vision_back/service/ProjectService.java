package com.vision_back.vision_back.service;

import java.util.List;
import java.util.TreeMap;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
public interface ProjectService {
    
    public HttpEntity<Void> setHeadersProject();
    
    public String getProjectBySlug(String slugProject);

    public Integer getProjectId();

    public void processProject();

    public List<Integer> processProjectList();

    public void processRoles();

    public List<Integer> processRolesList();
    
    public List<TreeMap<String, Object>> listAllProjectsByUser(Integer userCode);
    
    public void saveOnDatabaseProject(Integer projectCode, String projectName);

    public Integer getSpecificProjectUserRoleId();
}
