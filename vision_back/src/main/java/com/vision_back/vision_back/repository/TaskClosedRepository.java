package com.vision_back.vision_back.repository;


import com.vision_back.vision_back.entity.TaskStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskClosedRepository extends JpaRepository <TaskStatusHistoryEntity, Integer>{
    @Query("SELECT COUNT(t) FROM TaskStatusHistoryEntity t WHERE t.taskId.user.userId = :userId AND t.taskId.project.projectId = :projectId AND t.statsId.statusName = 'CLOSED'")
    Long countClosedTasksByUserAndProject(Integer userId, Integer projectId);
}
