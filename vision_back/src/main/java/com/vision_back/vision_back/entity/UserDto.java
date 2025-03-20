package com.vision_back.vision_back.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user")

public class UserDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_code")
    private Long userCode;

    @Column(name = "user_name")
    private String userName;  

    @Column(name = "user_role")
    private String userRole;

    @Column(name = "user_email")
    private String userEmail;  

    @Column(name = "user_team")
    private String userTeam;  
}