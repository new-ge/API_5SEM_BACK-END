package com.vision_back.vision_back.controller;

import com.vision_back.vision_back.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("users-and-tasks/project/{projectId}")
    public List<Map<String, Object>> getUsersAndTasks(@PathVariable Integer projectId) {
        return userService.getUsersAndTasks(projectId);
    }
}
