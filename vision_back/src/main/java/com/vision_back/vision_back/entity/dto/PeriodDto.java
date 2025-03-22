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
    private Long periodId;
    private Long periodCode;
    private Long periodDate;  
    private Long periodMonth;
    private Long periodYear;
    private Long periodHour; 
}