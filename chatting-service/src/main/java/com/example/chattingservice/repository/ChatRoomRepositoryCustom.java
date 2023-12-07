package com.example.chattingservice.repository;

import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.dto.RoomUserDto;
import com.example.chattingservice.vo.RoomUserState;
import com.example.chattingservice.entity.ChatRoom;

import java.util.List;

public interface ChatRoomRepositoryCustom {

    ChatRoom findByRoomId(String roomUuid);

    long updateRoomUserState(RoomUserState userState, ChatDto chatDto);

    long updateRecentMessageData(ChatDto chatDto);

    long updateReadMessageCount(ChatDto chatDto);

    ChatRoom findChatRoomByUserId(List<RoomUserDto> roomUserDtoList);

    List<ChatRoom> findAllChatRoomByUserId(String userUuid); //수정

    String findUsernameByRoomIdAndUserId(String roomUuid, String userUuid); // 수정

}
