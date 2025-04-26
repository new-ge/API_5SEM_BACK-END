package com.vision_back.vision_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.dto.TagDto;
import com.vision_back.vision_back.entity.TagEntity;

@Repository
public interface TagRepository extends JpaRepository<TagEntity,Integer>{
    Optional<TagEntity> findByTaskCodeAndProjectCodeAndTagNameAndQuant(TaskEntity taskCode, ProjectEntity projectCode, String tagName, Integer quant);

    @Query(value = "SELECT tag_name, SUM(quant) from usr_tag group by tag_name", nativeQuery = true)
    List<TagDto> countTasksByTag();

    boolean existsByUserTagIdIsNotNull();

    boolean existsByTaskCodeAndProjectCodeAndTagNameAndQuant(TaskEntity taskCode, ProjectEntity projectCode,
            String tagName, Integer quant);
}
