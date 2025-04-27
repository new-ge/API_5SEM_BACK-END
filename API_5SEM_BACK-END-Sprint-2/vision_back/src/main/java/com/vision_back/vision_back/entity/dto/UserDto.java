package com.vision_back.vision_back.entity.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserDto {
    private Integer taskId;
    private Integer userCode;
    private String userName;  
    private List<String> userRole;  
    private String userEmail; 

    public UserDto(Integer userId, String userName, List<String> userRole, String userEmail) {
        this.userCode = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.userEmail = userEmail;
        
    }
}
