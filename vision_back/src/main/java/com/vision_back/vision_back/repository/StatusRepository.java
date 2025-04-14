package com.vision_back.vision_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.StatusEntity;

@Repository
public interface StatusRepository extends JpaRepository<StatusEntity,Integer>{
    Optional<StatusEntity> findByStatusCodeAndStatusName(Integer statusCode, String statusName);

    Optional<StatusEntity> findByStatusName(String statusName);
}
