package com.example.userservice.dto;


import com.example.userservice.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 빠진 데이터는 NULL
public class ResponseUser {

    private String username;
    private String email;
    private String userId;

    private List<ResponseOrder> orders;

    public ResponseUser(UserEntity userEntity){
        this.username = userEntity.getUsername();
        this.email = userEntity.getEmail();
        this.userId = userEntity.getUserId();
    }

}
