package com.vision_back.vision_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.UserTaskEntity;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTaskEntity,Integer>{
    
}
