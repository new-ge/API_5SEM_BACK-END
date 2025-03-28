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
    @JoinColumn(name = "task_id")
    private TaskEntity taskId;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "stats_id")
    private StatusEntity statsId;

    @NotNull
    @Column(name = "change_date")
    private Timestamp changeDate;
}
