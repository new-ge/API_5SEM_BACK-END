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
@Table(name="usr_task")
public class UserTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_task_id")
    private Integer taskUserId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "task_code", referencedColumnName = "task_code")
    private TaskEntity taskCode;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_code", referencedColumnName = "project_code")
    private ProjectEntity projectCode;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usr_code", referencedColumnName = "usr_code")
    private UserEntity userCode;

    // @NotNull
    // @ManyToOne
    // @JoinColumn(name = "period_code", referencedColumnName = "period_code")
    // private PeriodEntity periodCode;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "stats_code", referencedColumnName = "stats_code")
    private StatusEntity statsCode;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "role_code", referencedColumnName = "role_code")
    private RoleEntity roleCode;
    
    @NotNull
    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "quant")
    private Integer quant;

    @Column(name = "average_time", insertable = false, updatable = false)
    private Integer averageTime;

    public UserTaskEntity(TaskEntity taskCode, ProjectEntity projectCode, UserEntity userCode, StatusEntity statsCode, RoleEntity roleCode, Timestamp startDate, Timestamp endDate, Integer quant) {
        this.taskCode = taskCode;
        this.projectCode = projectCode;
        this.userCode = userCode;
        // this.periodCode = periodCode;
        this.statsCode = statsCode;
        this.roleCode = roleCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.quant = quant;
    }
}
