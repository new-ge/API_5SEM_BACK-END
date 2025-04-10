package com.vision_back.vision_back.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDto {
    private Integer projectId;
    private Integer projectCode;
    private String projectName; 

    public ProjectDto(Integer projectCode, String projectName) {
        this.projectCode = projectCode;
        this.projectName = projectName;
    }
}
