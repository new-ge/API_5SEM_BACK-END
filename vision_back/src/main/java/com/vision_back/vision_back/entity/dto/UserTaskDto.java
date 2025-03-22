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
    private Long taskUserId;
    private Long taskId;
    private Long projectId;
    private Long userId;
    private Long periodId;
    private Long statsId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long quant;
    private Long rework;
    private Long averageTime;
}
