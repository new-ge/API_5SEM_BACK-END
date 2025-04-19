package com.vision_back.vision_back.entity.dto;

import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RoleDto {
    private Integer userRoleId;
    private UserEntity userCode;
    private ProjectEntity projectCode;
    private String userRole;
}