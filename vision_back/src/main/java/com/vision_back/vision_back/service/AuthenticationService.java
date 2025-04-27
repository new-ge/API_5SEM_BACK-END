package com.vision_back.vision_back.service;

<<<<<<< Updated upstream
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    public ResponseEntity<String> consumeAuthentication(String password, String username);

    public String getTokenAuthentication(String password, String username);
=======

public interface AuthenticationService {
    String getTokenAuthentication(String password, String username) throws Exception;
    String authenticateAndGetRole(String username, String password) throws Exception;
>>>>>>> Stashed changes
}
