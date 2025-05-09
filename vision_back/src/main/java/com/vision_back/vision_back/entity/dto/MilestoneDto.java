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
    private String userName;  
    private String projectName; 
    private String milestoneName;  
    private Long quant;

    public MilestoneDto(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    @Override
    public String toString() {
        return "MilestoneDto{" +
            "statusName='" + milestoneName + '\'' +
            ", count=" + quant +
            '}';
    }
}