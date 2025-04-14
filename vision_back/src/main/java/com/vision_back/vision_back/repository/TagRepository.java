package com.vision_back.vision_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.TagEntity;
@Repository
public interface TagRepository extends JpaRepository<TagEntity,Integer>{
    Optional<TagEntity> findByTaskCodeAndProjectCodeAndTagNameAndQuant(TaskEntity taskCode, ProjectEntity projectCode, String tagName, Integer quant);
}
