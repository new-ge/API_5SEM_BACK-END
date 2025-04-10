package com.vision_back.vision_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.ProjectEntity;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity,Integer>{
    
}
