package com.example.chattingservice.dto;


import com.example.chattingservice.vo.ChatRoomRequest;
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

    public static RoomUserDto getSenderInstance(ChatRoomRequest chatRoomRequest){
        return RoomUserDto.builder()
                .userUuid(chatRoomRequest.getFromUuid())
                .userNickname(chatRoomRequest.getFromNickname())
                .build();
    }

    public static RoomUserDto getReceiverInstance(ChatRoomRequest chatRoomRequest){
        return RoomUserDto.builder()
                .userUuid(chatRoomRequest.getTargetUuid())
                .userNickname(chatRoomRequest.getTargetNickname())
                .build();
    }


}
