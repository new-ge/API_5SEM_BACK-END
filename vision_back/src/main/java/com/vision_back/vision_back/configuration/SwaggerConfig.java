package com.vision_back.vision_back.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi tasksOnlyApi() {
        return GroupedOpenApi.builder()
                .group("tasks")
                .pathsToMatch("/tasks/**")
                .build();
    }

    @Bean
    public GroupedOpenApi projectsOnlyApi() {
        return GroupedOpenApi.builder()
                .group("projects")
                .pathsToMatch("/projects/**")
                .build();
    }

    @Bean
    public GroupedOpenApi usersOnlyApi() {
        return GroupedOpenApi.builder()
                .group("users")
                .pathsToMatch("/users/**")
                .build();
    }
}