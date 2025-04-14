package com.vision_back.vision_back.entity;

import java.sql.Timestamp;

import jakarta.persistence.*;

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
    @JoinColumn(name = "stats_code", referencedColumnName = "stats_code")
    private StatusEntity statsCode;

    @NotNull
    @Column(name = "change_date")
    private Timestamp changeDate;

    public TaskStatusHistoryEntity(TaskEntity taskCode, StatusEntity statsCode, Timestamp changeDate) {
        this.taskCode = taskCode;
        this.statsCode = statsCode;
        this.changeDate = changeDate;
    }
}
