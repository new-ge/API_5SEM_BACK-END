package com.vision_back.vision_back.service;

import java.util.List;

import org.springframework.http.HttpEntity;

public interface UserService {
    public HttpEntity<Void> setHeadersProject();

    public List<Integer> getUserId(Integer projectId);
}
