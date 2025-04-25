package com.vision_back.vision_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.TaskEntity;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity,Integer> {
    Optional<TaskEntity> findByTaskCode(Integer taskCode);

    Optional<TaskEntity> findByTaskCodeAndTaskDescription(Integer taskCode, String taskDescription);

    boolean existsByTaskIdIsNotNull();

    Boolean existsByTaskCodeAndTaskDescription(Integer taskCode, String taskDescription);
}
