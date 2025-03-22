package com.vision_back.vision_back.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vision_back.vision_back.service.UserStoriesServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("/user-stories")
public class UserStoriesController {

    @GetMapping("/count-user-stories-by-status/{projectId}")
    public Map<String, Integer> countUserStoriesByStatus(@PathVariable String projectId) {
        UserStoriesServiceImpl ussImpl = new UserStoriesServiceImpl();
        return ussImpl.countUserStoriesById(projectId);
    }
}