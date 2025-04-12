package com.vision_back.vision_back.entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="usr_tag")
public class UserTagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_tag_id")
    private Integer taskUserId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity taskId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity projectId;

    @NotNull
    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "quant")
    private Integer quant;

    public UserTagEntity(TaskEntity taskId, ProjectEntity projectId, String tagName, Integer quant) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.tagName = tagName;
        this.quant = quant;
    }
}