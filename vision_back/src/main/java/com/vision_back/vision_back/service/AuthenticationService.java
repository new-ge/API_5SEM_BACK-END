package com.vision_back.vision_back.service;

import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    public ResponseEntity<String> consumeAuthentication(String password, String username);

    public String getTokenAuthentication(String password, String username);
}
