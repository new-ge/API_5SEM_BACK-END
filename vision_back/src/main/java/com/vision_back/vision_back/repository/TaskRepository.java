package com.vision_back.vision_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.dto.MilestoneDto;
import com.vision_back.vision_back.entity.dto.TaskDto;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity,Integer> {
    Optional<TaskEntity> findByTaskCode(Integer taskCode);

    Optional<TaskEntity> findByTaskCodeAndTaskDescription(Integer taskCode, String taskDescription);

    boolean existsByTaskIdIsNotNull();

    boolean existsByTaskCodeAndTaskDescription(Integer taskCode, String taskDescription);

    @Query(value = """
                select u.usr_name, p.project_name, m.milestone_name, SUM(ut.quant) 
                from usr_task ut
                join task t on ut.task_code = t.task_code
                join milestone m on m.milestone_code = ut.milestone_code 
                JOIN usr u ON u.usr_code = ut.usr_code
                join project p on p.project_code = ut.project_code
                where ut.end_date is not null
                and u.is_logged_in = 1
                group by u.usr_name, p.project_name, m.milestone_name
                order by m.milestone_name asc 
                """, nativeQuery = true)
    List<TaskDto> countTasksDoneOperator();

    @Query(value = """
                select u.usr_name, p.project_name, m.milestone_name, SUM(ut.quant) 
                from usr_task ut
                join task t on ut.task_code = t.task_code
                join milestone m on m.milestone_code = ut.milestone_code 
                JOIN usr u ON u.usr_code = ut.usr_code 
                join project p on p.project_code = ut.project_code
                where ut.end_date is not null 
                group by u.usr_name, p.project_name, m.milestone_name
                order by m.milestone_name asc 
                """, nativeQuery = true)
    List<TaskDto> countTasksDoneManager();

    boolean existsByTaskCode(Integer taskCode);

    @Query(value = "SELECT * FROM task t WHERE t.task_code IS NOT NULL", nativeQuery = true)
    List<TaskEntity> findAllWithTaskCode();

    @Query(value = "select max(u.usr_name) as \"usr_name\", p.project_name, m.milestone_name, SUM(ut.quant) as \"sum_start\" \r\n" +
            "from usr_task ut \r\n" +
            "join task t on ut.task_code = t.task_code \r\n" +
            "join milestone m on m.milestone_code = ut.milestone_code \r\n" +
            "JOIN usr u ON u.usr_code = ut.usr_code \r\n" +
            "join project p on p.project_code = ut.project_code\r\n" +
            "where ut.start_date is not null \r\n" +
                    "AND (:milestone IS NULL OR m.milestone_name = :milestone) \r\n" +
                    "AND (:project IS NULL OR p.project_name = :project) \r\n" + 
                    "AND (:user IS NULL OR u.usr_name = :user) \r\n" +
            "group by u.usr_name, p.project_name, m.milestone_name", nativeQuery = true)
    List<MilestoneDto> countTaskscreatedAdmin(@Param("milestone") String milestone,
                                        @Param("project") String project,
                                        @Param("user") String user);
}