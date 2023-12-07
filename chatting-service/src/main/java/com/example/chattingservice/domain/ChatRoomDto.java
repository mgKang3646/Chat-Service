package com.example.chattingservice.domain;


import lombok.*;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class ChatRoomDto {

    private String roomUuid;
    private String recentMessage;
    private String recentDate;
    private int messageCount;
    private List<RoomUserDto> roomUsers;

    public static ChatRoomDto getInstance(){
        return ChatRoomDto.builder()
                .roomUuid(UUID.randomUUID().toString())
                .build();
    }

}
