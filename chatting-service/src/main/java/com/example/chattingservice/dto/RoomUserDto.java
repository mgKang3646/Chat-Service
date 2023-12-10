package com.example.chattingservice.dto;


import com.example.chattingservice.vo.ChatRoomCreateRequest;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class RoomUserDto {

    private String userUuid ;
    private String userNickname;
    private int readMessageCount;

    public static RoomUserDto getSenderInstance(ChatRoomCreateRequest chatRoomCreateRequest){
        return RoomUserDto.builder()
                .userUuid(chatRoomCreateRequest.getFromUuid())
                .userNickname(chatRoomCreateRequest.getFromNickname())
                .build();
    }

    public static RoomUserDto getReceiverInstance(ChatRoomCreateRequest chatRoomCreateRequest){
        return RoomUserDto.builder()
                .userUuid(chatRoomCreateRequest.getTargetUuid())
                .userNickname(chatRoomCreateRequest.getTargetNickname())
                .build();
    }


}
