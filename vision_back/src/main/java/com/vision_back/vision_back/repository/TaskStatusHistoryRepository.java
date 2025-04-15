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

@Repository
public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistoryEntity,Integer>{

    Optional<TaskStatusHistoryEntity> findByTaskCodeAndStatsCodeAndChangeDate(TaskEntity taskCode, StatusEntity statsCode,
            Timestamp changeDate);

    @Query(value = "SELECT subquery.* FROM (SELECT tsh.*, s.stats_name AS status_atual, CASE WHEN s.stats_name = 'Closed' AND LEAD(s.stats_name) OVER (PARTITION BY tsh.task_code ORDER BY tsh.change_date) <> 'Closed' THEN 1 ELSE 0 END AS rework_flag FROM task_status_history tsh LEFT JOIN stats s ON tsh.stats_code = s.stats_code) AS subquery WHERE subquery.rework_flag <> 0", nativeQuery = true)
    List<Object[]> findTaskStatusHistoryWithReworkFlagNative();
}
