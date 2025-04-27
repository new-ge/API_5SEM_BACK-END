package com.vision_back.vision_back.entity.dto;

import com.vision_back.vision_back.entity.ProjectEntity;
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
    private ProjectEntity projectCode;
    private String userRole;

    public RoleDto(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "RoleDto{" +
            "userRole='" + userRole + 
            '}';
    }
}