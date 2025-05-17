package com.vision_back.vision_back.controller;

import com.vision_back.vision_back.entity.dto.UserTaskAverageDTO;
import com.vision_back.vision_back.repository.UserRepository;
import com.vision_back.vision_back.service.AuthenticationService;
import com.vision_back.vision_back.service.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Users", description = "Endpoints relacionados aos usuários")
@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    @Autowired
    private AuthenticationService auth;

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticationControl(@RequestParam String username, @RequestParam String password) {
        try {
            String token = auth.getTokenAuthentication(password, username);
            String role = userService.getUserRole();

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