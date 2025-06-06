package com.vision_back.vision_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.ProjectEntity;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity,Integer>{
    Optional<ProjectEntity> findByProjectCode(Integer projectCode);

    Optional<ProjectEntity> findByProjectCodeAndProjectName(Integer projectCode, String projectName);

    boolean existsByProjectCodeIn(List<Integer> projectCodes);

    boolean existsByProjectIdIsNotNull();

    boolean existsByProjectCodeAndProjectName(Integer projectCode, String projectName);

    @Query(value = "select project_name from project", nativeQuery = true)
    List<String> listAllProjectName();

    boolean existsByProjectCode(Integer projectCode);
}
