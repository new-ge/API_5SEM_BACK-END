package com.vision_back.vision_back.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public HttpEntity<Void> setHeadersProject();

    public Integer getUserId();
}
