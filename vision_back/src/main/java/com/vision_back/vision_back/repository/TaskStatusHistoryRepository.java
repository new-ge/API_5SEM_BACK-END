package com.vision_back.vision_back.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.TaskStatusHistoryEntity;
import com.vision_back.vision_back.entity.dto.TaskStatusHistoryDto;

@Repository
public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistoryEntity,Integer>{

    Optional<TaskStatusHistoryEntity> findByTaskCodeAndLastStatusAndActualStatusAndChangeDate(TaskEntity taskCode, String lastStatus, String actualStatus,
            Timestamp changeDate);

    @Query(value = "SELECT tsh.*, CASE WHEN last_status = 'Closed' AND actual_status <> 'Closed' THEN 1 ELSE 0 END AS rework FROM task_status_history tsh WHERE last_status = 'Closed' AND actual_status <> 'Closed'", nativeQuery = true)
    List<TaskStatusHistoryDto> findTaskStatusHistoryWithReworkFlagNative();
}
