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
@Table(name="tag")
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Integer tagId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "task_code")
    private TaskEntity taskCode;

    @NotNull
    @Column(name = "tag_name")
    private String tagName;

    public TagEntity(TaskEntity taskCode, String tagName) {
        this.taskCode = taskCode;
        this.tagName = tagName;
    }
}