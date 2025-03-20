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
@Table(name="user_tag")

public class UserTagDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tag_id")
    private Long UserTagId;

    @Column(name = "task_code")
    private String taskCode;

    @Column(name = "tag_code")
    private String tagCode;  
}