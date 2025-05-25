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
import com.vision_back.vision_back.entity.RoleEntity;
import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.entity.UserTaskEntity;
import com.vision_back.vision_back.entity.dto.UserTaskAverageDTO;
import com.vision_back.vision_back.entity.dto.UserTaskDto;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTaskEntity,Integer>{

    Optional<UserTaskEntity> findByTaskCodeAndProjectCodeAndUserCodeAndStatsCodeAndRoleCode(
        TaskEntity taskCode, ProjectEntity projectCode, UserEntity userCode, StatusEntity statsCode, RoleEntity roleCode);

    Optional<UserTaskEntity> findByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndStatsCodeAndRoleCodeAndStartDateAndEndDate(
        TaskEntity taskCode, ProjectEntity projectCode, UserEntity userCode, MilestoneEntity milestoneCode,
        StatusEntity statsCode, RoleEntity roleCode, Timestamp startDate, Timestamp endDate);

    Optional<UserTaskEntity> findByTaskCodeAndProjectCodeAndUserCodeAndStatsCodeAndRoleCodeAndStartDate(
        TaskEntity taskCode, ProjectEntity projectCode, UserEntity userCode, StatusEntity statsCode,
        RoleEntity roleCode, Timestamp startDate);

    Optional<UserTaskDto> findByStatsCode(StatusEntity statsCode);

    boolean existsByTaskUserIdIsNotNull();

    boolean existsByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndStatsCodeAndRoleCodeAndStartDateAndEndDate(
        TaskEntity taskCode, ProjectEntity projectCode, UserEntity userCode, MilestoneEntity milestoneCode,
        StatusEntity statsCode, RoleEntity roleCode, Timestamp startDate, Timestamp endDate);

    boolean existsByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndStatsCodeAndRoleCode(TaskEntity taskCode,
            ProjectEntity projectCode, UserEntity userCode, MilestoneEntity milestoneCode,
            StatusEntity statsCode, RoleEntity roleCode);

        @Query(value = """
            SELECT m.milestone_name AS milestoneName,
                p.project_name AS projectName,
                u.usr_name AS userName, 
                AVG(ut.average_time) AS tempoMedio
            FROM usr_task ut
            JOIN usr u ON u.usr_code = ut.usr_code
            JOIN milestone m ON m.milestone_code = ut.milestone_code
            JOIN project p ON p.project_code = ut.project_code
            WHERE (:milestone IS NULL OR m.milestone_name = :milestone)
            AND (:project IS NULL OR p.project_name = :project)
            AND u.usr_name = :user
            AND ut.end_date IS NOT NULL
            AND u.is_logged_in = 1
        GROUP BY m.milestone_name, p.project_name, u.usr_name
        ORDER BY m.milestone_name
        """, nativeQuery = true)
        List<UserTaskAverageDTO> findAverageTimeByFiltersOperador(
            @Param("milestone") String milestone,
            @Param("project") String project,
            @Param("user") String user
);


        @Query(value = """
            SELECT m.milestone_name AS milestoneName,
                p.project_name AS projectName,
                u.usr_name AS userName, 
                AVG(ut.average_time) AS quant
            FROM usr_task ut
            JOIN usr u ON u.usr_code = ut.usr_code
            JOIN milestone m ON m.milestone_code = ut.milestone_code
            JOIN project p ON p.project_code = ut.project_code
            WHERE (:milestone IS NULL OR m.milestone_name = :milestone)
            AND p.project_name = :project
            AND (:user IS NULL OR u.usr_name = :user)
            AND ut.end_date IS NOT NULL
        GROUP BY m.milestone_name, p.project_name, u.usr_name
        ORDER BY m.milestone_name
        """, nativeQuery = true)
        List<UserTaskAverageDTO> findAverageTimeByFiltersManager(
            @Param("milestone") String milestone,
            @Param("project") String project,
            @Param("user") String user
        );

        @Query(value = """
        SELECT m.milestone_name AS milestoneName,
            p.project_name AS projectName,
            max(u.usr_name) AS userName, 
            AVG(ut.average_time) AS quant
        FROM usr_task ut
        JOIN usr u ON u.usr_code = ut.usr_code
        JOIN milestone m ON m.milestone_code = ut.milestone_code
        JOIN project p ON p.project_code = ut.project_code
        WHERE (:milestone IS NULL OR m.milestone_name = :milestone)
        AND (:project IS NULL OR p.project_name = :project)
        AND (:user IS NULL OR u.usr_name = :user)
        AND ut.end_date IS NOT NULL
        GROUP BY m.milestone_name, p.project_name
        ORDER BY m.milestone_name
        """, nativeQuery = true)
        List<UserTaskAverageDTO> findAverageTimeByFiltersAdmin(
            @Param("milestone") String milestone,
            @Param("project") String project,
            @Param("user") String user
        );
}

