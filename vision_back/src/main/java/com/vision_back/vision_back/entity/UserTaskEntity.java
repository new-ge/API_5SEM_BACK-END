package com.vision_back.vision_back.entity;

import java.sql.Timestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="usr_task")
public class UserTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_task_id")
    private Integer taskUserId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity projectId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TagEntity tagCode;

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

    @NotNull
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity roleId;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "quant")
    private Integer quant;

    @Column(name = "rework")
    private Integer rework;

    @Column(name = "average_time")
    private Integer averageTime;
}
