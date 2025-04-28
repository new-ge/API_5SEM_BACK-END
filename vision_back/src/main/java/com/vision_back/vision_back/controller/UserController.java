package com.vision_back.vision_back.controller;

import com.vision_back.vision_back.configuration.TokenConfiguration;
import com.vision_back.vision_back.service.AuthenticationService;
import com.vision_back.vision_back.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    @Autowired
    private AuthenticationService auth;

    @Autowired
    private TokenConfiguration tokenConf;


    @Autowired
    private UserServiceImpl userService;

    @GetMapping("users-and-tasks/project/{projectId}")
    public List<Map<String, Object>> getUsersAndTasks(@PathVariable Integer projectId) {
        return userService.getUsersAndTasks(projectId);
    }

    @GetMapping("users-and-tasks-per-sprint-name/project/{projectId}/sprint/{sprintName}")
    public List<Map<String, Object>> getUsersAndTasksPerSprintName(
        @PathVariable Integer projectId,
        @PathVariable String sprintName) {
        return userService.getUsersAndTasksPerSprintName(projectId, sprintName);
    }
    

    @PostMapping("/login")
    public ResponseEntity<?> authenticationControl(@RequestParam String password, @RequestParam String username) {
        try {
            String token = tokenConf.getAuthToken();
            String role = auth.authenticateAndGetRole(username, password);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", role);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha inválidos!");
        }
    }
}
