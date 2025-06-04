package com.gestion_obras.models.dtos.user;

import com.gestion_obras.models.enums.RoleType;
import lombok.Getter;

@Getter
public class UserInfoDto {
    private Integer id;
    private String numberID;
    private String fullName;
    private String email;
    private RoleType role;
}