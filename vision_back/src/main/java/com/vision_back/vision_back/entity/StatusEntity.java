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
@Table(name="stats")
public class StatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stats_id")
    private Integer statusId;

    @NotNull
    @Column(name = "stats_code")
    private Integer statusCode;

    @NotNull
    @Column(name = "stats_name")
    private String statusName;

    public StatusEntity(Integer statusCode, String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }
}