package com.vision_back.vision_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.UserEntity;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer>{
    Optional<UserEntity> findByUserCode(Integer userCode);

    Optional<UserEntity> findByUserCodeAndUserNameAndUserRoleAndUserEmail(
        Integer userCode, String userName, String[] userRole, String userEmail
    );

    boolean existsByUserCodeAndUserNameAndUserRoleAndUserEmail(Integer userCode, String userName, String[] userRole, String userEmail);

    boolean existsByUserCode(Integer userCode);

    @Transactional
    @Modifying
    @Query(value = "UPDATE usr SET is_logged_in = :isLogged WHERE usr_code = :userCode", nativeQuery = true)
    void updateIsLogged(@Param("isLogged") Integer isLogged, @Param("userCode") Integer userCode);

    @Transactional
    @Modifying
    @Query(value = "UPDATE usr SET is_logged_in = 0", nativeQuery = true)
    void setAllUsersLoggedOut();

    @Query(value = "SELECT usr_role from usr WHERE is_logged_in = 1", nativeQuery = true)
    List<String> accessControl();
}
