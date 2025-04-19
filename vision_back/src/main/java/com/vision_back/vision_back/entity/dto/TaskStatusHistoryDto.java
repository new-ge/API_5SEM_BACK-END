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
    private Integer id;
    private Integer taskCode;
    private String lastStatus;
    private String actualStatus;
    private Timestamp changeDate;
    private Integer rework;
    
    public TaskStatusHistoryDto(Integer taskCode, String lastStatus, String actualStatus, Timestamp changeDate, Integer rework) {
        this.taskCode = taskCode;
        this.lastStatus = lastStatus;
        this.actualStatus = actualStatus;
        this.changeDate = changeDate;
        this.rework = rework;
    }

    @Override
    public String toString() {
        return "TaskStatusHistoryDto{" +
                "id=" + id +
                ", taskCode='" + taskCode + '\'' +
                ", lastStatus='" + lastStatus + '\'' +
                ", actualStatus='" + actualStatus + '\'' +
                ", changeDate=" + changeDate +
                ", rework=" + rework +
                '}';
    }
}