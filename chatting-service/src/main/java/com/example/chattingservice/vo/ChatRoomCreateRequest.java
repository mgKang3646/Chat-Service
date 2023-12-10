package com.example.chattingservice.vo;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ChatRoomCreateRequest {

    @NotNull(message = "{required.request.create.targetUuid}")
    private String targetUuid; //필수값 => Get방식으로 전환될 예정
    private String targetNickname; //서비스간 통신
    private String roomUuid; // 서버에서 생성
    private String fromUuid; // 헤더값
    private String fromNickname; // 서비스간 통신

}
