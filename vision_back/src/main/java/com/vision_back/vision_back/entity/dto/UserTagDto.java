package com.vision_back.vision_back.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserTagDto {
    private Integer taskUserId;
    private Integer taskId;
    private Integer tagId;
    private Integer projectId;
    private Integer quant;
}
