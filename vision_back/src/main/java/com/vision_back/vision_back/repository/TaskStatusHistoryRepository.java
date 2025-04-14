package com.vision_back.vision_back.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.TaskStatusHistoryEntity;

@Repository
public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistoryEntity,Integer>{

    Optional<TaskStatusHistoryEntity> findByTaskCodeAndStatsCodeAndChangeDate(TaskEntity taskCode, StatusEntity statsCode,
            Timestamp changeDate);
    
}
