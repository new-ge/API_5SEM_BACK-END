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
@Table(name="milestone")
public class MilestoneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "milestone_id")
    private Integer milestoneId;

    @NotNull
    @Column(name = "milestone_code")
    private Integer milestoneCode;

    @NotNull
    @Column(name = "milestone_name")
    private String milestoneName;  

    @NotNull
    @Column(name = "estimated_start")
    private Timestamp estimatedStart;

    @NotNull
    @Column(name = "estimated_end")
    private Timestamp estimatedEnd;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "project_code", referencedColumnName = "project_code")
    private ProjectEntity projectCode;  

    public MilestoneEntity(Integer milestoneCode, String milestoneName, Timestamp estimatedStart, Timestamp estimatedEnd, ProjectEntity projectCode) {
        this.milestoneCode = milestoneCode;
        this.milestoneName = milestoneName;
        this.estimatedStart = estimatedStart;
        this.estimatedEnd = estimatedEnd;
        this.projectCode = projectCode;
    }
}