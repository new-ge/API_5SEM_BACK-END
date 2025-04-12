package com.vision_back.vision_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vision_back.vision_back.entity.TaskEntity;

public interface TaskRepository extends JpaRepository<TaskEntity,Integer> {
    Optional<TaskEntity> findByTaskCode(Integer taskCode);
}
