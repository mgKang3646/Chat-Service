package com.example.chattingservice.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomRequest {
    private String roomUuid;
    private String fromUuid;
    private String fromNickname;
    private String targetUuid;
    private String targetNickname;
}
