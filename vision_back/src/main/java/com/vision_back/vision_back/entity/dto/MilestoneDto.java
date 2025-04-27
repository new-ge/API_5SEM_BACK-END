package com.vision_back.vision_back.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class MilestoneDto {
    private Integer milestoneId;
    private String userName;  
    private String milestoneName;  
    private String projectName;  
    private Long quant;

    public MilestoneDto(String projectName, String userName, String milestoneName, Long quant) {
        this.projectName = projectName;
        this.userName = userName;
        this.milestoneName = milestoneName;
        this.quant = quant;
    }
}