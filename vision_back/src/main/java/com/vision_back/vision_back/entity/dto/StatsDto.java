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
    private String statusName;
    private Long quant;

    public StatsDto(String statusName, Long quant) {
        this.statusName = statusName;
        this.quant = quant;
    }

    @Override
    public String toString() {
        return "StatsDto{" +
            "statusName='" + statusName + '\'' +
            ", count=" + quant +
            '}';
    }
}