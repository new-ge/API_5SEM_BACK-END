package com.vision_back.vision_back.entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="usr_tag")
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_tag_id")
    private Integer userTagId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "task_code", referencedColumnName = "task_code")
    private TaskEntity taskCode;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_code", referencedColumnName = "project_code")
    private ProjectEntity projectCode;

    @NotNull
    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "quant")
    private Integer quant;

    public TagEntity(TaskEntity taskCode, ProjectEntity projectCode, String tagName, Integer quant) {
        this.taskCode = taskCode;
        this.projectCode = projectCode;
        this.tagName = tagName;
        this.quant = quant;
    }
}