package com.vision_back.vision_back.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PeriodDto {
    private Integer periodId;
    private Integer periodCode;
    private Integer periodDate;  
    private Integer periodMonth;
    private Integer periodYear;
    private Integer periodHour; 
}