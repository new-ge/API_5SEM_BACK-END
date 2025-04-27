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

    @NotNull
    @Column(name = "last_status")
    private String lastStatus;

    @NotNull
    @Column(name = "actual_status")
    private String actualStatus;

    @NotNull
    @Column(name = "change_date")
    private Timestamp changeDate;

    public TaskStatusHistoryEntity(TaskEntity taskCode, String lastStatus, String actualStatus, Timestamp changeDate) {
        this.taskCode = taskCode;
        this.lastStatus = lastStatus;
        this.actualStatus = actualStatus;
        this.changeDate = changeDate;
    }
}
