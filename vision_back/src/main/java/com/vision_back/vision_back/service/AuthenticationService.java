package com.vision_back.vision_back.service;

<<<<<<< HEAD
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
=======
import org.springframework.stereotype.Service;
@Service
public interface AuthenticationService {
    public void getTokenAuthentication(String password, String username);
>>>>>>> 71c7d93dd61ff73b11b8badc3d6f324d3a2423e3
}
