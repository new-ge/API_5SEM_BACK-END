package com.vision_back.vision_back.service;

import org.springframework.http.HttpEntity;

public interface ProjectService {
    
    public HttpEntity<Void> setHeadersProject();
    
    public String getProjectBySlug(String slugProject);
}
