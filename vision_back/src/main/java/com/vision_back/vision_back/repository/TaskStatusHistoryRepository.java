package com.vision_back.vision_back.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

            @Query(value = 
            "WITH ranked_tasks AS ( \r\n" +
            "   SELECT \r\n" +
            "       u.usr_name, \r\n" +
            "       p.project_name, \r\n" +
            "       m.milestone_name, \r\n" +
            "       tsh.task_code, \r\n" +
            "       tsh.last_status, \r\n" +
            "       tsh.actual_status, \r\n" +
            "       ROW_NUMBER() OVER (PARTITION BY tsh.task_code ORDER BY tsh.task_code DESC) AS rn \r\n" +
            "   FROM task_status_history tsh \r\n" +
            "   JOIN milestone m ON m.milestone_code = tsh.milestone_code \r\n" +
            "   JOIN usr u ON u.usr_code = tsh.usr_code \r\n" +
            "   JOIN project p ON p.project_code = tsh.project_code \r\n" +
            "   WHERE u.is_logged_in = 1 \r\n" +
            "   AND (:milestone IS NULL OR m.milestone_name = :milestone) \r\n" +
            "   AND (:project IS NULL OR p.project_name = :project) \r\n" +
            "   AND (:user IS NULL OR u.usr_name = :user) \r\n" +
            ") \r\n" +
            "SELECT \r\n" +
            "   usr_name, \r\n" +
            "   project_name, \r\n" +
            "   milestone_name, \r\n" +
            "   CAST(CASE WHEN last_status = 'Closed' AND actual_status <> 'Closed' THEN 1 ELSE 0 END AS BIGINT) AS rework, \r\n" +
            "   CAST(CASE WHEN actual_status = 'Closed' AND rn = 1 THEN 1 ELSE 0 END AS BIGINT) AS finished \r\n" +
            "FROM ranked_tasks \r\n", nativeQuery = true)
    List<TaskStatusHistoryDto> findTaskStatusHistoryWithReworkFlagOperator(@Param("milestone") String milestone,
                                                                           @Param("project") String project,
                                                                           @Param("user") String user);

        @Query(value = 
        "WITH ranked_tasks AS ( \r\n" +
        "   SELECT \r\n" +
        "       u.usr_name, \r\n" +
        "       p.project_name, \r\n" +
        "       m.milestone_name, \r\n" +
        "       tsh.task_code, \r\n" +
        "       tsh.last_status, \r\n" +
        "       tsh.actual_status, \r\n" +
        "       ROW_NUMBER() OVER (PARTITION BY tsh.task_code ORDER BY tsh.task_code DESC) AS rn \r\n" +
        "   FROM task_status_history tsh \r\n" +
        "   JOIN milestone m ON m.milestone_code = tsh.milestone_code \r\n" +
        "   JOIN usr u ON u.usr_code = tsh.usr_code \r\n" +
        "   JOIN project p ON p.project_code = tsh.project_code \r\n" +
        "   WHERE (:milestone IS NULL OR m.milestone_name = :milestone) \r\n" +
        "   AND (:project IS NULL OR p.project_name = :project) \r\n" +
        "   AND (:user IS NULL OR u.usr_name = :user) \r\n" +
        ") \r\n" +
        "SELECT \r\n" +
        "   usr_name, \r\n" +
        "   project_name, \r\n" +
        "   milestone_name, \r\n" +
        "   CAST(CASE WHEN last_status = 'Closed' AND actual_status <> 'Closed' THEN 1 ELSE 0 END AS BIGINT) AS rework, \r\n" +
        "   CAST(CASE WHEN actual_status = 'Closed' AND rn = 1 THEN 1 ELSE 0 END AS BIGINT) AS finished \r\n" +
        "FROM ranked_tasks", nativeQuery = true)
    List<TaskStatusHistoryDto> findTaskStatusHistoryWithReworkFlagManager(@Param("milestone") String milestone,
                                                                          @Param("project") String project,
                                                                          @Param("user") String user);

    boolean existsByStatusHistoryIdIsNotNull();

    boolean existsByTaskCodeAndLastStatusAndActualStatusAndChangeDate(TaskEntity taskCode, String lastStatus,
            String actualStatus, Timestamp changeDate);

    boolean existsByTaskCodeAndMilestoneCodeAndUserCodeAndLastStatusAndActualStatusAndChangeDate(TaskEntity taskOpt,
            MilestoneEntity milestoneOpt, UserEntity userOpt, String ultimoStatus, String statusAtual, Timestamp from);

    boolean existsByTaskCodeAndProjectCodeAndMilestoneCodeAndUserCodeAndLastStatusAndActualStatusAndChangeDate(
            TaskEntity taskOpt, ProjectEntity projectOpt, MilestoneEntity milestoneOpt, UserEntity userOpt,
            String ultimoStatus, String statusAtual, Timestamp from);

}
