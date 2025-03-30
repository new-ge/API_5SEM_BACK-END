package com.vision_back.vision_back.entity.dto;

import java.sql.Timestamp;

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
    private Integer taskId;
    private Integer statsId;
    private Timestamp changeDate;
}