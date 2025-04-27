package com.vision_back.vision_back.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenConfiguration {
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
