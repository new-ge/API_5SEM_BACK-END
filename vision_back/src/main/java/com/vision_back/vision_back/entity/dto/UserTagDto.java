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
    private Long taskUserId;
    private Long taskId;
    private Long tagId;
    private Long projectId;
    private Long quant;
}
