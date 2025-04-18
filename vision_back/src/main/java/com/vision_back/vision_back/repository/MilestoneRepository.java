package com.vision_back.vision_back.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.MilestoneEntity;

@Repository
public interface MilestoneRepository extends JpaRepository<MilestoneEntity,Integer> {
     Optional<MilestoneEntity> findByMilestoneCodeAndMilestoneNameAndEstimatedStartAndEstimatedEnd(Integer milestoneCode, String milestoneName, Timestamp estimatedStart, Timestamp estimatedEnd);
}
