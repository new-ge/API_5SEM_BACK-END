package com.vision_back.vision_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer>{
    Optional<UserEntity> findByUserCode(Integer userCode);

    Optional<UserEntity> findByUserCodeAndUserNameAndUserRoleAndUserEmail(
        Integer userCode, String userName, String[] userRole, String userEmail
    );

    boolean existsByUserCodeAndUserNameAndUserRoleAndUserEmail(Integer userCode, String userName, String[] userRole, String userEmail);
}
