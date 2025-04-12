package com.vision_back.vision_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.ProjectEntity;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity,Integer>{
    Optional<ProjectEntity> findByProjectCode(Integer projectCode);
}
