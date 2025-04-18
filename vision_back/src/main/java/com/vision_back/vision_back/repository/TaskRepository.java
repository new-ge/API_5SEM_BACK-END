package com.vision_back.vision_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.dto.TagDto;
import com.vision_back.vision_back.entity.dto.UserTaskDto;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity,Integer> {
    Optional<TaskEntity> findByTaskCode(Integer taskCode);

    // @Query()
    // Optional<TagDto> countTasksByStatusClosed();
}
