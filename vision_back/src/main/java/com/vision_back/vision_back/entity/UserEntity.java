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
@Table(name="usr")

public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_id")
    private Long userId;

    @NotNull
    @Column(name = "usr_code")
    private Long userCode;

    @NotNull
    @Column(name = "usr_name")
    private String userName;  

    @NotNull
    @Column(name = "usr_role")
    private Long userRole;  

    @NotNull
    @Column(name = "usr_email")
    private String userEmail;  
}