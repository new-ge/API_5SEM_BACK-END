package com.vision_back.vision_back.entity;

import javax.persistence.*;

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
@Table(name="project")

public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @NotNull
    @Column(name = "project_code")
    private Long projectCode;

    @NotNull
    @Column(name = "project_name")
    private String projectName;  
}