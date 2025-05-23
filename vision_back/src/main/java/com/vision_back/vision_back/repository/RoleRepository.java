package com.vision_back.vision_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Integer> {
    Optional<RoleEntity> findByRoleCodeAndRoleName(Integer roleCode, String roleName);
    
    Optional<RoleEntity> findByRoleCode(Integer roleCode);

    Optional<RoleEntity> findByRoleCodeAndRoleNameAndProjectCode(Integer roleCode, String roleName, ProjectEntity projectCode);
    
    boolean existsByRoleIdIsNotNull();

    boolean existsByRoleCodeAndRoleName(Integer roleCode, String roleName);
}
