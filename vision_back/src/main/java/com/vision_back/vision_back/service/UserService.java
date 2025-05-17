package com.vision_back.vision_back.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import com.vision_back.vision_back.entity.dto.UserTaskAverageDTO;

@Service
public interface UserService {
    public HttpEntity<Void> setHeadersProject();

    public String getUserRole();

    public Integer getUserId();

    public void processUser();

    public void verifyIfIsLogged(Integer userCode, Integer isLogged);

    List<String> accessControl();

}
