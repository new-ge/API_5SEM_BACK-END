package com.vision_back.vision_back.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.MilestoneEntity;
import com.vision_back.vision_back.entity.dto.MilestoneDto;

@Repository
public interface MilestoneRepository extends JpaRepository<MilestoneEntity,Integer> {
     Optional<MilestoneEntity> findByMilestoneCode(Integer milestoneCode);

     Optional<MilestoneEntity> findByMilestoneCodeAndMilestoneNameAndEstimatedStartAndEstimatedEnd(Integer milestoneCode, String milestoneName, LocalDate estimatedStart, LocalDate estimatedEnd);

     @Query(value = "select u.usr_name, p.project_name, m.milestone_name, SUM(ut.quant)\r\n" + 
                    "from usr_task ut \r\n" + 
                    "join milestone m on m.milestone_code = ut.milestone_code \r\n" + 
                    "JOIN usr u ON u.usr_code = ut.usr_code \r\n" + 
                    "join project p on p.project_code = ut.project_code  \r\n" + 
                    "where u.is_logged_in = 1 \r\n" + 
                    "group by u.usr_name, m.milestone_name, p.project_name", nativeQuery = true)
     List<MilestoneDto> countCardsPerSprintOperator();

     @Query(value = "select u.usr_name, p.project_name, m.milestone_name, SUM(ut.quant)\r\n" + 
                    "from usr_task ut \r\n" + 
                    "join milestone m on m.milestone_code = ut.milestone_code \r\n" +
                    "JOIN usr u ON u.usr_code = ut.usr_code \r\n" + 
                    "join project p on p.project_code = ut.project_code  \r\n" + 
                    "where ut.end_date is not null \r\n" + 
                    "and u.is_logged_in = 1 \r\n" + 
                    "group by u.usr_name, p.project_name, m.milestone_name", nativeQuery = true)
     List<MilestoneDto> countCardsClosedPerSprintOperator();

     @Query(value = "select u.usr_name, p.project_name, m.milestone_name, SUM(ut.quant)\r\n" + 
                    "from usr_task ut \r\n" + 
                    "join milestone m on m.milestone_code = ut.milestone_code \r\n" + 
                    "JOIN usr u ON u.usr_code = ut.usr_code \r\n" + 
                    "join project p on p.project_code = ut.project_code \r\n" + 
                    "group by u.usr_name, m.milestone_name, p.project_name", nativeQuery = true)
     List<MilestoneDto> countCardsPerSprintManager();

     @Query(value = "select u.usr_name, p.project_name, m.milestone_name, SUM(ut.quant)\r\n" + 
                    "from usr_task ut \r\n" + 
                    "join milestone m on m.milestone_code = ut.milestone_code \r\n" + 
                    "JOIN usr u ON u.usr_code = ut.usr_code \r\n" + 
                    "join project p on p.project_code = ut.project_code  \r\n" + 
                    "where ut.end_date is not null \r\n" + 
                    "group by u.usr_name, p.project_name, m.milestone_name", nativeQuery = true)
     List<MilestoneDto> countCardsClosedPerSprintManager();

     @Query(value = "select milestone_name from milestone", nativeQuery = true)
     List<String> listAllSprintName();

     boolean existsByMilestoneIdIsNotNull();

     boolean existsByMilestoneCodeAndMilestoneNameAndEstimatedStartAndEstimatedEnd(Integer milestoneCode, String milestoneName, LocalDate estimatedStart, LocalDate estimatedEnd);

     @Query(value="select u.usr_name, p.project_name, m.milestone_name, sum(ut.average_time) \r\n" + 
                    "from usr_task ut \r\n" + 
                    "join milestone m on m.milestone_code = ut.milestone_code \r\n" + 
                    "JOIN usr u ON u.usr_code = ut.usr_code \r\n" + 
                    "join project p on p.project_code = ut.project_code  \r\n" + 
                    "group by u.usr_name, p.project_name, m.milestone_name", nativeQuery = true)
     List<MilestoneDto> averageTaskTimePerSprintManager(); 

     @Query(value="select u.usr_name, p.project_name, m.milestone_name, sum(ut.average_time) \r\n" + 
                    "from usr_task ut \r\n" + 
                    "join milestone m on m.milestone_code = ut.milestone_code \r\n" + 
                    "JOIN usr u ON u.usr_code = ut.usr_code \r\n" + 
                    "join project p on p.project_code = ut.project_code  \r\n" +
                    "and u.is_logged_in = 1 \r\n" + 
                    "group by u.usr_name, p.project_name, m.milestone_name", nativeQuery = true)
     List<MilestoneDto> averageTaskTimePerSprintOperator();
}
