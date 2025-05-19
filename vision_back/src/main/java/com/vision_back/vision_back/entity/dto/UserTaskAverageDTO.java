package com.vision_back.vision_back.entity.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class UserTaskAverageDTO {
    private String milestoneName;
    private String projectName;
    private String userName;
    private BigDecimal quant;
}
