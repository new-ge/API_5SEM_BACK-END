package com.vision_back.vision_back.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StatsDto {
    private Integer statusId;
    private String userName;
    private String projectName;
    private String statusName;
    private String milestoneName;
    private Long quant;

    public StatsDto(String userName, String projectName, String statusName, String milestoneName, Long quant) {
        this.userName = userName;
        this.projectName = projectName;
        this.milestoneName = milestoneName;
        this.statusName = statusName;
        this.quant = quant;
    }
}