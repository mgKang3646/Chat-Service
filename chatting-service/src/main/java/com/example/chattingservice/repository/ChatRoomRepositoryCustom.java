package com.example.chattingservice.repository;

import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.dto.RoomUserCreateDto;
import com.example.chattingservice.dto.RoomUserFindDto;
import com.example.chattingservice.vo.RoomUserState;
import com.example.chattingservice.entity.ChatRoom;

import java.util.List;

public interface ChatRoomRepositoryCustom {

    ChatRoom findChatRoomWithRoomUserByRoomId(String roomUuid);

    long updateRoomUserState(RoomUserState userState, ChatDto chatDto);

    long updateRecentMessageData(ChatDto chatDto);

    long updateReadMessageCount(ChatDto chatDto);

    ChatRoom findChatRoomByUserId(RoomUserFindDto roomUserFindDto);

    List<ChatRoom> findAllChatRoomByUserId(String userUuid); //수정

    String findUsernameByRoomIdAndUserId(String roomUuid, String userUuid); // 수정

}
