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
    private String taskDescription;
    private Long quant;  

    public TaskDto(String taskDescription, Long quant) {
        this.taskDescription = taskDescription;
        this.quant = quant;
    }
}