package com.vision_back.vision_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.TagEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.UserTagEntity;
import com.vision_back.vision_back.entity.UserTaskEntity;

public interface UserTagRepository extends JpaRepository<UserTagEntity,Integer>{
    Optional<UserTagEntity> findByTaskIdAndProjectId(TaskEntity taskId, ProjectEntity projectId);
}
