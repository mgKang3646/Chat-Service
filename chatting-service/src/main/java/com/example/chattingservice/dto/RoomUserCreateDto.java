package com.example.chattingservice.dto;


import com.example.chattingservice.vo.ChatRoomCreateRequest;
import com.example.chattingservice.vo.RoomUserState;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class RoomUserCreateDto {

    private String userUuid ;
    private String userNickname;
    private int readMessageCount;
    private RoomUserState userState;

    public static RoomUserCreateDto getSenderInstance(ChatRoomCreateRequest chatRoomCreateRequest){
        return RoomUserCreateDto.builder()
                .userUuid(chatRoomCreateRequest.getFromUuid())
                .userNickname(chatRoomCreateRequest.getFromNickname())
                .userState(RoomUserState.CREATE)
                .build();
    }


    public static RoomUserCreateDto getReceiverInstance(ChatRoomCreateRequest chatRoomCreateRequest){
        return RoomUserCreateDto.builder()
                .userUuid(chatRoomCreateRequest.getTargetUuid())
                .userNickname(chatRoomCreateRequest.getTargetNickname())
                .userState(RoomUserState.CREATE)
                .build();
    }



}
