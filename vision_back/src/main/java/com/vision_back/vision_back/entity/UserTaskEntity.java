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
    @JoinColumn(name = "task_code")
    private TagEntity taskCode;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_code")
    private ProjectEntity projectCode;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usr_code")
    private UserEntity userCode;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "period_code")
    private PeriodEntity periodCode;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "stats_code")
    private StatusEntity statsCode;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "role_code")
    private RoleEntity roleCode;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "quant")
    private Integer quant;

    @Column(name = "average_time")
    private Integer averageTime;

    public UserTaskEntity(TagEntity taskCode, ProjectEntity projectCode, UserEntity userCode, PeriodEntity periodCode, StatusEntity statsCode, RoleEntity roleCode, Timestamp startDate, Timestamp endDate, Integer quant, Integer averageTime) {
        this.taskCode = taskCode;
        this.projectCode = projectCode;
        this.userCode = userCode;
        this.periodCode = periodCode;
        this.statsCode = statsCode;
        this.roleCode = roleCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.quant = quant;
        this.averageTime = averageTime;
    }
}
