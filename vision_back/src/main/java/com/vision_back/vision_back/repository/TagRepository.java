package com.vision_back.vision_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.MilestoneEntity;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.entity.dto.TagDto;
import com.vision_back.vision_back.entity.TagEntity;

@Repository
public interface TagRepository extends JpaRepository<TagEntity,Integer>{
    Optional<TagEntity> findByTaskCodeAndProjectCodeAndTagNameAndQuant(TaskEntity taskCode, ProjectEntity projectCode, String tagName, Integer quant);

    @Query(value = "select u.usr_name, p.project_name, m.milestone_name, utag.tag_name, SUM(utag.quant) \r\n" + 
                        "from usr_tag utag \r\n" + 
                        "join milestone m on m.milestone_code = utag.milestone_code \r\n" + 
                        "JOIN usr u ON u.usr_code = utag.usr_code \r\n" + 
                        "join project p on p.project_code = utag.project_code  \r\n" + 
                        "where u.is_logged_in = 1 \r\n" + 
                        "AND (:milestone IS NULL OR m.milestone_name = :milestone) \r\n" +
                        "AND (:project IS NULL OR p.project_name = :project) \r\n" + 
                        "AND (:user IS NULL OR u.usr_name = :user) \r\n" + 
                        "group by u.usr_name, m.milestone_name, p.project_name, utag.tag_name", nativeQuery = true)
    List<TagDto> countTasksByTagOperator(@Param("milestone") String milestone,
                                         @Param("project") String project,
                                         @Param("user") String user);

    @Query(value = "select u.usr_name, p.project_name, m.milestone_name, utag.tag_name, SUM(utag.quant) \r\n" + 
                        "from usr_tag utag \r\n" + 
                        "join milestone m on m.milestone_code = utag.milestone_code \r\n" + 
                        "JOIN usr u ON u.usr_code = utag.usr_code \r\n" + 
                        "join project p on p.project_code = utag.project_code  \r\n" + 
                        "where (:milestone IS NULL OR m.milestone_name = :milestone) \r\n" +
                        "AND (:project IS NULL OR p.project_name = :project) \r\n" + 
                        "AND (:user IS NULL OR u.usr_name = :user) \r\n" + 
                        "group by u.usr_name, m.milestone_name, p.project_name, utag.tag_name", nativeQuery = true)
    List<TagDto> countTasksByTagManager(@Param("milestone") String milestone,
                                        @Param("project") String project,
                                        @Param("user") String user);

    boolean existsByUserTagIdIsNotNull();

    boolean existsByTaskCodeAndProjectCodeAndUserCodeAndTagNameAndQuant(TaskEntity taskCode, ProjectEntity projectCode, UserEntity userCode,
            String tagName, Integer quant);

    boolean existsByTagName(String tagName);

    boolean findByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndTagName(TaskEntity taskCode,
    ProjectEntity projectCode, UserEntity userCode, MilestoneEntity taskSprintCode, String tagName);

    boolean existsByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndTagName(TaskEntity taskCode,
    ProjectEntity projectCode, UserEntity userCode, MilestoneEntity taskSprintCode, String tagName);

    boolean existsByTaskCodeAndProjectCodeAndUserCodeAndMilestoneCodeAndTagNameAndQuant(TaskEntity taskCode,
            ProjectEntity projectCode, UserEntity userCode, MilestoneEntity milestoneEntity, String tagName,
            Integer quant);

        @Query(value = "select max(u.usr_name), p.project_name, m.milestone_name, utag.tag_name, SUM(utag.quant) \r\n" +
                        "from usr_tag utag \r\n" +
                        "join milestone m on m.milestone_code = utag.milestone_code \r\n" +
                        "JOIN usr u ON u.usr_code = utag.usr_code \r\n" +
                        "join project p on p.project_code = utag.project_code \r\n" +
                        "group by u.usr_name, m.milestone_name, p.project_name, utag.tag_name", nativeQuery = true)
        List<TagDto> countTasksByTagAdmin();
}
