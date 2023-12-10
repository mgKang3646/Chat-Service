package com.example.chattingservice.dto;


import com.example.chattingservice.vo.ChatRoomCreateRequest;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomUserFindDto {

    private String fromUuid;
    private String targetUuid;

    public static RoomUserFindDto convert(ChatRoomCreateRequest roomCreateRequest){
        return RoomUserFindDto.builder()
                .fromUuid(roomCreateRequest.getFromUuid())
                .targetUuid(roomCreateRequest.getTargetUuid())
                .build();
    }
}
