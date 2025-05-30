package com.vision_back.vision_back.entity.dto;

import java.sql.Timestamp;

import com.vision_back.vision_back.entity.MilestoneEntity;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.RoleEntity;
import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.UserEntity;

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
    private TaskEntity taskCode;
    private ProjectEntity projectCode;
    private UserEntity userCode;
    private MilestoneEntity milestoneCode;
    private StatusEntity statsCode;
    private RoleEntity roleCode;
    private Timestamp startDate;
    private Timestamp endDate;
    private Integer quant;
    private Integer averageTime;

    public UserTaskDto(TaskEntity taskCode, ProjectEntity projectCode, UserEntity userCode, MilestoneEntity milestoneCode, StatusEntity statsCode, RoleEntity roleCode, Timestamp startDate, Timestamp endDate, Integer quant) {
        this.taskCode = taskCode;
        this.projectCode = projectCode;
        this.userCode = userCode;
        this.milestoneCode = milestoneCode;
        this.statsCode = statsCode;
        this.roleCode = roleCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.quant = quant;
    }

    @Override
    public String toString() {
        return "UserTaskDto{" +
                "projectCode=" + projectCode +
                ", userCode='" + userCode + '\'' +
                ", taskCode='" + taskCode + '\'' +
                ", milestoneCode='" + milestoneCode + '\'' +
                ", statsCode='" + statsCode + '\'' +
                ", roleCode=" + roleCode +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", quant=" + quant +
                '}';
    }
}
