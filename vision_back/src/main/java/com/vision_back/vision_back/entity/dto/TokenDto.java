package com.vision_back.vision_back.entity.dto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
public class TokenDto {
    private String authToken;

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    @Bean
    public String getAuthToken() {
        return authToken;
    }

    public boolean hasToken() {
        return authToken != null;
    }
}
