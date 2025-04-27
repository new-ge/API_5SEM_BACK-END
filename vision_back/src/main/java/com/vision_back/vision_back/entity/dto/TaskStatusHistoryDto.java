package com.vision_back.vision_back.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TaskStatusHistoryDto {
    private Integer statusHistoryId;
    private String userName;
    private String projectName;
    private String milestoneName;
    private String lastStatus;
    private String actualStatus;
    private Long rework;
        
    public TaskStatusHistoryDto(String userName, String projectName, String milestoneName, String lastStatus, String actualStatus, Long rework) {
        this.userName = userName;
        this.projectName = projectName;
        this.milestoneName = milestoneName;
        this.lastStatus = lastStatus;
        this.actualStatus = actualStatus;
        this.rework = rework;
    }
}