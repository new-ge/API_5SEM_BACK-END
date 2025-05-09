package com.vision_back.vision_back.repository;

import java.sql.Timestamp;
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
    SELECT AVG(average_time)
    FROM usr_task
    WHERE usr_code = :userId AND end_date IS NOT NULL
    """, nativeQuery = true)
    Double findAverageExecutionTimeByUserId(@Param("userId") Integer userId);

}

