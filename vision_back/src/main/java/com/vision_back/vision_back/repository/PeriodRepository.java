package com.vision_back.vision_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.PeriodEntity;

@Repository
public interface PeriodRepository extends JpaRepository<PeriodEntity,Integer>{
    
}
