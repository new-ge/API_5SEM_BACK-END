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
@Entity
@Table(name="project")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Integer projectId;

    @NotNull
    @Column(name = "project_code")
    private Integer projectCode;

    @NotNull
    @Column(name = "project_name")
    private String projectName; 
    
    public ProjectEntity(Integer projectCode, String projectName) {
        this.projectCode = projectCode;
        this.projectName = projectName;
    }
}