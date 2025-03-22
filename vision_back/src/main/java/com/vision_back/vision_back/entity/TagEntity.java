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
@Table(name="tag")

public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;
    
    @NotNull
    @Column(name = "tag_code")
    private Long tagCode;

    @NotNull
    @Column(name = "tag_description")
    private String tagDescription;
}