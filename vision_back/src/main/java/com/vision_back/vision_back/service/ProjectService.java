package com.vision_back.vision_back.service;

import java.util.List;
import java.util.TreeMap;

import org.springframework.http.HttpEntity;


public interface ProjectService {
    
    public HttpEntity<Void> setHeadersProject();
    
    public String getProjectBySlug(String slugProject);

    public String getProjectId(Integer memberId);

    public List<TreeMap<String, Object>> listAllProjectsByUser(Integer userId);
    
}
