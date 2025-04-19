package com.vision_back.vision_back.entity.dto;

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
    private Integer userRole;  
    private String userEmail; 

    public UserDto(Integer userId, String userName, String userEmail, Integer userRole) {
        this.userCode = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
    }
}
