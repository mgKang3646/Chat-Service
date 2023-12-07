package com.example.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String username;
    private String email;
    private String userId;
    private String createdAt;
    private String encryptedPwd;
    private String pwd;

}
