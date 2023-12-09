package com.example.chattingservice.service;

import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.vo.ChatRoomRequest;
import com.example.chattingservice.vo.ChatRoomResponse;
import com.example.chattingservice.vo.RoomUserState;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.List;

public interface ChatRoomService {

    public ChatRoomResponse createChatRoom(ChatRoomRequest chatRoomRequest);
    public void processSendMessage(ChatDto chatDto);
    public void updateRoomUserState(RoomUserState roomUserState, ChatDto chatDto);

    public void updateReadMessageCount(ChatDto chatDto);

    public ChatRoomResponse findOrCreateChatRoom(ChatRoomRequest chatRoomRequest);

    public List<ChatRoomResponse> findAllChatRoomByUserId(String userId);

    public void updateEnterUserState(RoomUserState roomUserState, ChatDto chatDto);

    public ChatDto doExitUserProcess(StompHeaderAccessor stompHeaderAccessor);

    public List<ChatDto> findAllChatList(String roomUuid);



}
