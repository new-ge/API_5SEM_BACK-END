package com.vision_back.vision_back.entity;

import java.util.List;

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
@Table(name="usr")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_id")
    private Integer userId;

    @NotNull
    @Column(name = "usr_code")
    private Integer userCode;

    @NotNull
    @Column(name = "usr_name")
    private String userName;  

    @NotNull
    @Column(name = "usr_role")
    private String[] userRole;    

    @NotNull
    @Column(name = "usr_email")
    private String userEmail;

    public UserEntity(Integer userCode, String userName, String[] userRole, String userEmail) {
        this.userCode = userCode;
        this.userName = userName;
        this.userRole = userRole;
        this.userEmail = userEmail;
    }
}