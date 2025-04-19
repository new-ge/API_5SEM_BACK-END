package com.vision_back.vision_back.repository;

import java.sql.Timestamp;
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

     @Query(value = "select m.milestone_name, SUM(ut.quant) from usr_task ut join milestone m on ut.milestone_code = m.milestone_code group by m.milestone_name order by m.milestone_name asc", nativeQuery = true)
     List<MilestoneDto> countCardsPerSprint();
}
