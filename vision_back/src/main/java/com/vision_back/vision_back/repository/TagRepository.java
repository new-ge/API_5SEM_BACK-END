package com.vision_back.vision_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.entity.dto.TagDto;
import com.vision_back.vision_back.entity.TagEntity;

@Repository
public interface TagRepository extends JpaRepository<TagEntity,Integer>{
    Optional<TagEntity> findByTaskCodeAndProjectCodeAndTagNameAndQuant(TaskEntity taskCode, ProjectEntity projectCode, String tagName, Integer quant);

    @Query(value = "SELECT tag_name, SUM(quant) from usr_tag utag JOIN usr u ON u.usr_code = utag.usr_code WHERE u.is_logged_in = 1 group by tag_name", nativeQuery = true)
    List<TagDto> countTasksByTagOperator();

    @Query(value = "SELECT tag_name, SUM(quant) from usr_tag utag JOIN usr u ON u.usr_code = utag.usr_code group by tag_name", nativeQuery = true)
    List<TagDto> countTasksByTagManager();

    boolean existsByUserTagIdIsNotNull();

    boolean existsByTaskCodeAndProjectCodeAndUserCodeAndTagNameAndQuant(TaskEntity taskCode, ProjectEntity projectCode, UserEntity userCode,
            String tagName, Integer quant);
}
