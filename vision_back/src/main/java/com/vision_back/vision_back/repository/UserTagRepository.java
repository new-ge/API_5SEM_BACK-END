package com.vision_back.vision_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vision_back.vision_back.entity.UserTagEntity;

public interface UserTagRepository extends JpaRepository<UserTagEntity,Integer>{
    @Query("SELECT ut.tag.id, SUM(ut.quant) FROM UserTag ut GROUP BY ut.tag.id")
    List<Object[]> countCardsByTag();
    
}
