package com.vision_back.vision_back.entity;

import java.sql.Time;
import java.time.Month;
import java.time.Year;
import java.util.Date;

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
@Table(name="task_user")

public class TaskUserDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_user_id")
    private Long taskUserId;

    @Column(name = "project_code")
    private Long projectCode;

    @Column(name = "user_code")
    private Date userCode;  

    @Column(name = "period_code")
    private Month periodCode;

    @Column(name = "status_code")
    private Year statusCode;

    @Column(name = "task_description")
    private Time taskDescription;  
}