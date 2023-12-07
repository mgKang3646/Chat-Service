package com.example.chattingservice.domain;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatDto {

    public enum MessageType {
        ENTER, TALK, LEAVE, EXITED;

    }

    private MessageType type;
    private String roomUuid;
    private String senderUuid;
    private String senderNickname;
    private String message;
    private String messageTime;

    public static ChatDto getInstanceEnter(ChatDto chatDtoRequest){
        return ChatDto.builder()
                .roomUuid(chatDtoRequest.getRoomUuid())
                .senderNickname(chatDtoRequest.getSenderNickname())
                .senderUuid(chatDtoRequest.getSenderUuid())
                .message(chatDtoRequest.getSenderNickname() + "님이 입장하였습니다.")
                .type(MessageType.ENTER)
                .build();
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
