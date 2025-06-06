package com.vision_back.vision_back.entity;

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
@Table(name="task")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

    @NotNull
    @Column(name = "task_code")
    private Integer taskCode;
    
    @NotNull
    @Column(name = "task_description")
    private String taskDescription;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "milestone_code", referencedColumnName = "milestone_code")
    private MilestoneEntity milestoneCode;   
    
    public TaskEntity(Integer taskCode, String taskDescription, MilestoneEntity milestoneCode) {
        this.taskCode = taskCode;
        this.taskDescription = taskDescription;
        this.milestoneCode = milestoneCode;
    }
}