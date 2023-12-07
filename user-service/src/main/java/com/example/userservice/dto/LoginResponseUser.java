package com.example.userservice.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseUser {

    private String jwt;
    private String userId;
    private String username;

    public static LoginResponseUser getLoginResponseUser(String jwt, UserDto userDto){
        return LoginResponseUser
                .builder()
                .jwt(jwt)
                .userId(userDto.getUserId())
                .username(userDto.getUsername())
                .build();
    }
}
