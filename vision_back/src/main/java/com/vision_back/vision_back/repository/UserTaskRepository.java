package com.vision_back.vision_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vision_back.vision_back.entity.UserTaskEntity;

public interface UserTaskRepository extends JpaRepository<UserTaskEntity,Integer>{
    
}
