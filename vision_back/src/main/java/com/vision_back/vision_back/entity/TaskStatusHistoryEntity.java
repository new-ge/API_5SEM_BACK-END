package com.vision_back.vision_back.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="task_status_history")

public class TaskStatusHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_history_id")
    private Long statusHistoryId;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "task_id")
    private TaskEntity taskId;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "stats_id")
    private StatusEntity statsId;

    @NotNull
    @Column(name = "change_date")
    private LocalDateTime changeDate;
}
