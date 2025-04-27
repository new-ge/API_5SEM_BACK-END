package com.vision_back.vision_back.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public HttpEntity<Void> setHeadersProject();

    public Integer getUserId();

    public void saveOnDatabaseUser(Integer userCode, String userDescription, String[] userRole, String userEmail, Integer isLogged);
}
