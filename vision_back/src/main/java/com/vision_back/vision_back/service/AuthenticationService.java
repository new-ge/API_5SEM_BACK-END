package com.vision_back.vision_back.service;

public interface AuthenticationService {
    String getTokenAuthentication(String password, String username) throws Exception;
    String authenticateAndGetRole(String username, String password) throws Exception;

}
