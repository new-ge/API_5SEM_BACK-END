package com.vision_back.vision_back.entity;

import javax.persistence.*;

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
    @JoinColumn(name = "tag_id")
    private TagEntity tagId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity projectId;

    @Column(name = "quant")
    private Integer quant;
}