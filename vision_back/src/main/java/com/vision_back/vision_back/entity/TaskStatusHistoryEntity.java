package com.vision_back.vision_back.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="task_status_history")
public class TaskStatusHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_history_id")
    private Integer statusHistoryId;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "task_code", referencedColumnName = "task_code")
    private TaskEntity taskCode;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "usr_code", referencedColumnName = "usr_code")
    private UserEntity userCode;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "project_code", referencedColumnName = "project_code")
    private ProjectEntity projectCode;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "milestone_code", referencedColumnName = "milestone_code")
    private MilestoneEntity milestoneCode;

    @NotNull
    @Column(name = "last_status")
    private String lastStatus;

    @NotNull
    @Column(name = "actual_status")
    private String actualStatus;

    @NotNull
    @Column(name = "change_date")
    private Timestamp changeDate;

    public TaskStatusHistoryEntity(TaskEntity taskCode, UserEntity userCode, ProjectEntity projectCode, MilestoneEntity milestoneCode, String lastStatus, String actualStatus, Timestamp changeDate) {
        this.taskCode = taskCode;
        this.userCode = userCode;
        this.projectCode = projectCode;
        this.milestoneCode = milestoneCode;
        this.lastStatus = lastStatus;
        this.actualStatus = actualStatus;
        this.changeDate = changeDate;
    }
}
