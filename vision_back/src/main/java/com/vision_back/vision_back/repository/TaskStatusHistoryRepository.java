package com.vision_back.vision_back.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.MilestoneEntity;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.TaskStatusHistoryEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.entity.dto.TaskStatusHistoryDto;

@Repository
public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistoryEntity,Integer>{

    Optional<TaskStatusHistoryEntity> findByTaskCodeAndLastStatusAndActualStatusAndChangeDate(TaskEntity taskCode, String lastStatus, String actualStatus,
            Timestamp changeDate);

    @Query(value = "SELECT u.usr_name, p.project_name, m.milestone_name, tsh.last_status, tsh.actual_status, \r\n" + //
                        "CAST(CASE WHEN last_status = 'Closed' AND actual_status <> 'Closed' THEN 1 ELSE 0 END AS BIGINT) AS rework \r\n" + //
                        "FROM task_status_history tsh \r\n" + //
                        "join milestone m on m.milestone_code = tsh.milestone_code \r\n" + //
                        "JOIN usr u ON u.usr_code = tsh.usr_code \r\n" + //
                        "join project p on p.project_code = tsh.project_code \r\n" + //
                        "where u.is_logged_in = 1 \r\n" + //
                        "and last_status = 'Closed' \r\n" + //
                        "AND actual_status <> 'Closed'", nativeQuery = true)
    List<TaskStatusHistoryDto> findTaskStatusHistoryWithReworkFlagOperator();

    @Query(value = "SELECT u.usr_name, p.project_name, m.milestone_name, tsh.last_status, tsh.actual_status,\r\n" + //
                        "CAST(CASE WHEN last_status = 'Closed' AND actual_status <> 'Closed' THEN 1 ELSE 0 END AS BIGINT) AS rework \r\n" + //
                        "FROM task_status_history tsh\r\n" + //
                        "join milestone m on m.milestone_code = tsh.milestone_code \r\n" + //
                        "JOIN usr u ON u.usr_code = tsh.usr_code \r\n" + //
                        "join project p on p.project_code = tsh.project_code \r\n" + //
                        "WHERE last_status = 'Closed'\r\n" + //
                        "AND actual_status <> 'Closed'", nativeQuery = true)
    List<TaskStatusHistoryDto> findTaskStatusHistoryWithReworkFlagManager();

    boolean existsByStatusHistoryIdIsNotNull();

    boolean existsByTaskCodeAndUserCodeAndLastStatusAndActualStatusAndChangeDate(TaskEntity taskCode, UserEntity userCode, String lastStatus,
            String actualStatus, Timestamp changeDate);

    boolean existsByTaskCodeAndUserCodeAndProjectCodeAndMilestoneCodeAndLastStatusAndActualStatusAndChangeDate(
            TaskEntity taskCode, UserEntity userCode, ProjectEntity projectCode,
            MilestoneEntity milestoneCode, String lastStatus,
            String actualStatus, Timestamp changeDate);
}
