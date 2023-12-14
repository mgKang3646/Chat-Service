package com.example.chattingservice.repository;

import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.vo.RoomUserState;

public interface RoomUserRepositoryCustom {
    String findUsernameByRoomIdAndUserId(String roomUUID, String userUUID);

    long updateRoomUserState(RoomUserState roomUserState, ChatDto chatDtoExit);

    long updateReadMessageCount(ChatDto chatDto);
}
