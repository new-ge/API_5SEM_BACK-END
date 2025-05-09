package com.vision_back.vision_back.entity.dto;

import java.util.List;

import com.vision_back.vision_back.entity.UserEntity;

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
    private String[] userRole;
    private Integer isLogged; 

    public UserDto(Integer userCode, String userName, String[] userRole, Integer isLogged) {
        this.userCode = userCode;
        this.userName = userName;
        this.userRole = userRole;
        this.isLogged = isLogged;
    }

    public UserDto(Integer userCode) {
        this.userCode = userCode;
    }
}
