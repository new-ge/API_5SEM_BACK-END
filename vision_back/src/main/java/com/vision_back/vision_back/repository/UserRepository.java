package com.vision_back.vision_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vision_back.vision_back.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Integer>{
    
}
