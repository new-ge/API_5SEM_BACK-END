package com.vision_back.vision_back.entity;

import java.util.Arrays;

import com.vision_back.vision_back.entity.dto.UserDto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="usr")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_id")
    private Integer userId;

    @NotNull
    @Column(name = "usr_code")
    private Integer userCode;

    @NotNull
    @Column(name = "usr_name")
    private String userName;  

    @NotNull
    @Column(name = "usr_role")
    private String[] userRole;    

    @NotNull
    @Column(name = "is_logged_in")
    private Integer isLogged;

    public UserEntity(Integer userCode, String userName, String[] userRole, Integer isLogged) {
        this.userCode = userCode;
        this.userName = userName;
        this.userRole = userRole;
        this.isLogged = isLogged;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
            "userCode=" + userCode +
            ", userName='" + userName + '\'' +
            ", userRole=" + Arrays.toString(userRole) +
            ", isLogged=" + isLogged +
            '}';
    }

    public UserEntity convertToUserEntity(UserDto dto) {
        return new UserEntity(
            dto.getUserCode(),
            dto.getUserName(),
            dto.getUserRole(),
            dto.getIsLogged()
        );
    }
}