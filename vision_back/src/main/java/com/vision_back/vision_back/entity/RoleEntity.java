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
@Table(name="role")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @NotNull
    @Column(name = "role_code")
    private Integer roleCode;

    @NotNull
    @Column(name = "role_name")
    private String roleName;  

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_code", referencedColumnName = "project_code")
    private ProjectEntity projectCode;

    public RoleEntity(Integer roleCode, String roleName, ProjectEntity projectCode) {
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.projectCode = projectCode;
    }
}