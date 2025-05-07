package com.vision_back.vision_back.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public HttpEntity<Void> setHeadersProject();

    public String getUserRole();

    public Integer getUserId();

    public void processAllUsers();

    public void verifyIfIsLogged(Integer userCode, Integer isLogged);
}
