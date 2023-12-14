package com.example.chattingservice.service;

import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.vo.ChatRoomCreateRequest;
import com.example.chattingservice.vo.ChatRoomResponse;
import com.example.chattingservice.vo.RoomUserState;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRoomService {

    public ChatRoomResponse createChatRoom(ChatRoomCreateRequest chatRoomCreateRequest);
    public void processSendMessage(ChatDto chatDto);
    public void updateRoomUserState(RoomUserState roomUserState, ChatDto chatDto);

    public void updateReadMessageCount(ChatDto chatDto);

    public ChatRoomResponse findOrCreateChatRoom(ChatRoomCreateRequest chatRoomCreateRequest);

    public List<ChatRoomResponse> findAllChatRoomByUserId(String userId);

    public void updateEnterUserState(RoomUserState roomUserState, ChatDto chatDto);

    public ChatDto doExitUserProcess(StompHeaderAccessor stompHeaderAccessor);

    public Slice<ChatDto> findAllChatList(String roomUuid, LocalDateTime beforeTime);



}
