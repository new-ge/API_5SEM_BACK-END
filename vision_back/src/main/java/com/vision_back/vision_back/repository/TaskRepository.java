package com.vision_back.vision_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.dto.StatsDto;
import com.vision_back.vision_back.entity.dto.TaskDto;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity,Integer> {
    Optional<TaskEntity> findByTaskCode(Integer taskCode);

    Optional<TaskEntity> findByTaskCodeAndTaskDescription(Integer taskCode, String taskDescription);

    boolean existsByTaskIdIsNotNull();

    boolean existsByTaskCodeAndTaskDescription(Integer taskCode, String taskDescription);

    @Query(value = "select t.task_description, SUM(ut.quant) from usr_task ut join task t on ut.task_code = t.task_code where ut.end_date is not null group by t.task_description", nativeQuery = true)
    List<TaskDto> countTasksDone();
}
