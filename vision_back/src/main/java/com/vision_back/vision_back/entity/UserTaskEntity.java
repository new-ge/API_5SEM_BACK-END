package com.vision_back.vision_back.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="usr_task")
public class UserTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_task_id")
    private Long taskUserId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "task_id")
    private ProjectEntity projectId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_id")
    private TagEntity tagId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usr_id")
    private UserEntity userId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "period_id")
    private PeriodEntity periodId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "stats_id")
    private StatusEntity statsId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "quant")
    private Long quant;

    @Column(name = "rework")
    private Long rework;

    @Column(name = "average_time")
    private Long averageTime;
}
