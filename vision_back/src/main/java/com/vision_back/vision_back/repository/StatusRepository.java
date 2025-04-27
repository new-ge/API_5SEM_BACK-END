package com.vision_back.vision_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.dto.StatsDto;

@Repository
public interface StatusRepository extends JpaRepository<StatusEntity,Integer>{
    Optional<StatusEntity> findByStatusCodeAndStatusName(Integer statusCode, String statusName);

    Optional<StatusEntity> findByStatusName(String statusName);
    
    Optional<StatusEntity> findByStatusCode(Integer statusCode);

    @Query(value = "SELECT s.stats_name, SUM(ut.quant) FROM usr_task ut join stats s on s.stats_code = ut.stats_code JOIN usr u ON u.usr_code = ut.usr_code where u.is_logged_in = 1 group by s.stats_name", nativeQuery = true)
    List<StatsDto> countTasksByStatusOperator();

    @Query(value = "SELECT s.stats_name, SUM(ut.quant) FROM usr_task ut join stats s on s.stats_code = ut.stats_code JOIN usr u ON u.usr_code = ut.usr_code where s.stats_name = 'Closed' and u.is_logged_in = 1 group by s.stats_name", nativeQuery = true)
    List<StatsDto> countTasksByStatusClosedOperator();

    @Query(value = "SELECT s.stats_name, SUM(ut.quant) FROM usr_task ut join stats s on s.stats_code = ut.stats_code JOIN usr u ON u.usr_code = ut.usr_code group by s.stats_name", nativeQuery = true)
    List<StatsDto> countTasksByStatusManager();

    @Query(value = "SELECT s.stats_name, SUM(ut.quant) FROM usr_task ut join stats s on s.stats_code = ut.stats_code JOIN usr u ON u.usr_code = ut.usr_code where s.stats_name = 'Closed' group by s.stats_name", nativeQuery = true)
    List<StatsDto> countTasksByStatusClosedManager();


    boolean existsByStatusIdIsNotNull();
    
    boolean existsByStatusCodeAndStatusName(Integer statusCode, String statusName);
}