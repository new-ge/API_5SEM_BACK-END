package com.vision_back.vision_back.service;

import org.springframework.stereotype.Service;
@Service
public interface AuthenticationService {
    public String getTokenAuthentication(String password, String username);
    public String getCachedToken();
}
