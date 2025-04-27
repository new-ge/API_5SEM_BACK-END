package com.vision_back.vision_back.configuration;

import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class TokenConfiguration {
    private String authToken;

    public boolean hasToken() {
        return authToken != null;
    }
}
