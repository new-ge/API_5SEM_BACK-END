package com.vision_back.vision_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vision_back.vision_back.entity.TagEntity;
import com.vision_back.vision_back.entity.TaskEntity;

public interface TagRepository extends JpaRepository<TagEntity,Integer>{

}
