package com.example.chattingservice.dto;


import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatDto implements Serializable {

    public enum MessageType {
        ENTER, TALK, LEAVE, EXITED;

    }

    private MessageType type;
    private String roomUuid;
    private String senderUuid;
    private String senderNickname;
    private String message;
    private LocalDateTime messageTime;

    public void updateEnterMessage(){
        setMessage(getSenderNickname() + "님이 입장하였습니다.");
    }

    public static ChatDto getInstanceExit(String roomUUID, String userUUID, String username){
        return ChatDto.builder()
                .roomUuid(roomUUID)
                .senderUuid(userUUID)
                .senderNickname(username)
                .message(username + "님이 퇴장하였습니다.")
                .type(MessageType.EXITED)
                .build();
    }

}
