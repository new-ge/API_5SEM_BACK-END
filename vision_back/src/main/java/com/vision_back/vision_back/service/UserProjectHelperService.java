package com.vision_back.vision_back.service;

import org.springframework.stereotype.Service;

@Service
public interface UserProjectHelperService  {
    public Integer fetchLoggedUserId();

    public void processUsersByProjectId(Integer projectCode);

    public Integer fetchProjectIdByUserId(Integer userId);
    
}
