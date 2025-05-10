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

    @Operation(summary = "Tempo médio de execução dos cards por usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tempo médio calculado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })

    @GetMapping("/average-time")
    public ResponseEntity<List<UserTaskAverageDTO>> getAverageExecutionTime(
        @RequestParam(required = false) String milestone,
        @RequestParam(required = false) String project,
        @RequestParam(required = false) String user
    ) {
        try {
            List<String> accessList = userService.accessControl();
            List<UserTaskAverageDTO> result;

            if (accessList.contains("STAKEHOLDER")) {
                result = userService.getAverageExecutionTimeManager(milestone, project, user);
            } else {
                result = userService.getAverageExecutionTimeOperator(milestone, project, user);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.emptyList());
        }
    }
}