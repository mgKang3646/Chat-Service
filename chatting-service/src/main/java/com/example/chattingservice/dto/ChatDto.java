package com.example.chattingservice.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @NotNull(message = "{required.request}")
    private MessageType type; // 필수값
    @NotBlank(message = "{required.request}")
    private String roomUuid; // 필수값
    @NotBlank(message = "{required.request}")
    private String senderUuid; //필수값
    @NotNull(message = "{required.request}")
    private LocalDateTime messageTime; //필수값 + 바인딩 에러
    private String senderNickname; // 서비스 간 통신
    private String message;

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
