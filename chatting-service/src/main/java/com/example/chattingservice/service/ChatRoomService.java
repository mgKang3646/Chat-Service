package com.example.chattingservice.service;

import com.example.chattingservice.domain.*;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.List;

public interface ChatRoomService {

    public ChatRoomResponse createChatRoom(ChatRoomRequest chatRoomRequest);
    public String deleteUserInRoom(String roomId, String userId);
    public void findAllRoomByUserId();
    public void updateRecentMessageData(ChatDto chatDto);
    public ChatRoomDto getChatRoom(String roomId);
    public void updateRoomUserState(RoomUserState roomUserState, ChatDto chatDto);

    public void updateReadMessageCount(ChatDto chatDto);

    public String findChatRoomByUserId(ChatRoomRequest chatRoomRequest);

    public List<ChatRoomResponse> findAllChatRoomByUserId(String userId);

    public void updateEnterUserState(RoomUserState roomUserState, ChatDto chatDto);

    public ChatDto doExitUserProcess(StompHeaderAccessor stompHeaderAccessor);

    public String findUsername(String roomUUID, String userUUID);


}
