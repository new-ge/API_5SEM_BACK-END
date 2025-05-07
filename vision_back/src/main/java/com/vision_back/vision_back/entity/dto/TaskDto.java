package com.vision_back.vision_back.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TaskDto {
    private Integer taskId;
    private Integer taskCode;
    private String userName;
    private String projectName;
    private String milestoneName;
    private String taskDescription;
    private Long quant;  

    public TaskDto(String userName, String projectName, String milestoneName, Long quant) {
        this.userName = userName;
        this.projectName = projectName;
        this.milestoneName = milestoneName;
        this.quant = quant;
    }
}