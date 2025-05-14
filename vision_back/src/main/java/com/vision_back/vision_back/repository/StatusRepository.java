package com.vision_back.vision_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.dto.StatsDto;

@Repository
public interface StatusRepository extends JpaRepository<StatusEntity,Integer>{
    Optional<StatusEntity> findByStatusCodeAndStatusName(Integer statusCode, String statusName);

    Optional<StatusEntity> findByStatusName(String statusName);
    
    Optional<StatusEntity> findByStatusCode(Integer statusCode);

    @Query(value = "SELECT u.usr_name, p.project_name, m.milestone_name, s.stats_name, SUM(ut.quant) \r\n" + 
                "FROM usr_task ut \r\n" + 
                "join milestone m on m.milestone_code = ut.milestone_code \r\n" + 
                "JOIN usr u ON u.usr_code = ut.usr_code \r\n" + 
                "join project p on p.project_code = ut.project_code \r\n" + 
                "join stats s on s.stats_code = ut.stats_code \r\n" + 
                "where u.is_logged_in = 1 \r\n" + 
                "AND (:milestone IS NULL OR m.milestone_name = :milestone) \r\n" +
                "AND (:project IS NULL OR p.project_name = :project) \r\n" + 
                "AND (:user IS NULL OR u.usr_name = :user) \r\n" + 
                "group by s.stats_name, m.milestone_name, u.usr_name, p.project_name", nativeQuery = true)
    List<StatsDto> countTasksByStatusOperator(@Param("milestone") String milestone,
                                              @Param("project") String project,
                                              @Param("user") String user);

    @Query(value = "SELECT u.usr_name, p.project_name, m.milestone_name, s.stats_name, SUM(ut.quant) \r\n" + 
                "FROM usr_task ut \r\n" + 
                "join milestone m on m.milestone_code = ut.milestone_code \r\n" + 
                "JOIN usr u ON u.usr_code = ut.usr_code \r\n" + 
                "join project p on p.project_code = ut.project_code \r\n" + 
                "join stats s on s.stats_code = ut.stats_code \r\n" + 
                "WHERE (:milestone IS NULL OR m.milestone_name = :milestone) \r\n" +
                "AND (:project IS NULL OR p.project_name = :project) \r\n" + 
                "AND (:user IS NULL OR u.usr_name = :user) \r\n" + 
                "group by s.stats_name, m.milestone_name, u.usr_name, p.project_name", nativeQuery = true)
    List<StatsDto> countTasksByStatusManager(@Param("milestone") String milestone,
                                             @Param("project") String project,
                                             @Param("user") String user);

    boolean existsByStatusIdIsNotNull();
    
    boolean existsByStatusCodeAndStatusName(Integer statusCode, String statusName);

    boolean existsByStatusCode(Integer statsCode);

        @Query(value = "select max(u.usr_name), p.project_name, m.milestone_name, s.stats_name, SUM(ut.quant) \r\n" +
                "FROM usr_task ut \r\n" +
                "join milestone m on m.milestone_code = ut.milestone_code \r\n"+
                "JOIN usr u ON u.usr_code = ut.usr_code \r\n"+
                "join project p on p.project_code = ut.project_code \r\n"+
                "join stats s on s.stats_code = ut.stats_code \r\n"+
                "group by s.stats_name, m.milestone_name, p.project_name", nativeQuery = true)
    List<StatsDto> countTasksByStatusAdmin();
}