package com.vision_back.vision_back.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TagDto {
    private Integer taskUserId;
    private Integer taskId;
    private Integer tagId;
    private Integer projectId;
    private String userName;
    private String projectName;
    private String milestoneName;
    private String tagName;    
    private Long quant;

    public TagDto(String userName, String projectName, String milestoneName, String tagName, Long quant) {
        this.userName = userName;
        this.projectName = projectName;
        this.milestoneName = milestoneName;
        this.tagName = tagName;
        this.quant = quant;
    }
}
