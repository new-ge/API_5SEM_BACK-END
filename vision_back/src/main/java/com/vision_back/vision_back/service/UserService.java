package com.vision_back.vision_back.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import com.vision_back.vision_back.entity.UserEntity;

@Service
public interface UserService {
    public HttpEntity<Void> setHeadersProject();

    public Integer getUserId();

    public UserEntity saveOnDatabaseUser(Integer userCode, String userDescription, String[] userRole, String userEmail);
}
