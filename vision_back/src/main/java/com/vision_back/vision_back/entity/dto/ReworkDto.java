package com.vision_back.vision_back.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReworkDto {
    private String projectName;
    private String milestoneName;
    private String fromStatus;
    private String toStatus;
    private Long quant;
}
