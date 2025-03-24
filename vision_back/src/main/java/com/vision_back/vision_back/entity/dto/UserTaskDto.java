package com.vision_back.vision_back.entity.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserTaskDto {
    private Integer taskUserId;
    private Integer taskId;
    private Integer projectId;
    private Integer userId;
    private Integer periodId;
    private Integer statsId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer quant;
    private Integer rework;
    private Integer averageTime;
}
