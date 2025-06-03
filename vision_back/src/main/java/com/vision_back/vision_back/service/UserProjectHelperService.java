package com.vision_back.vision_back.service;

import org.springframework.stereotype.Service;

@Service
public interface UserProjectHelperService  {
    public Integer fetchLoggedUserId();

    public Integer loggedUserId();

    public String fetchLoggedUserName();

    public void processUsersByProjectId(Integer projectCode);

    public Integer fetchProjectIdByUserId(Integer userId);

    public String fetchProjectNameByUserId(Integer userId);

    public void verifyIfIsLogged(Integer userCode, Integer isLogged);
    
}
