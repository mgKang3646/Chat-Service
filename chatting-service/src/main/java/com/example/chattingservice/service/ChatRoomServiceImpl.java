package com.example.chattingservice.service;


import com.example.chattingservice.domain.*;
import com.example.chattingservice.entity.ChatRoom;
import com.example.chattingservice.entity.RoomUser;
import com.example.chattingservice.repository.ChatRoomRepository;
import com.example.chattingservice.repository.RoomUserRepository;
import com.example.chattingservice.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ModelMapperUtil modelMapperUtil;
    private final ChatRoomRepository chatRoomRepository;
    private final RoomUserRepository roomUserRepository;

    @Override
    public ChatRoomResponse createChatRoom(ChatRoomRequest chatRoomRequest) {
        ChatRoom chatRoom = modelMapperUtil.convertToChatRoom(chatRoomRequest);
        List<RoomUser> roomUserList = modelMapperUtil.convertToRoomUser(chatRoomRequest);

        // 영속화
        chatRoomRepository.save(chatRoom);
        roomUserList.forEach(roomUser -> {
            roomUser.updateChatRoom(chatRoom);
            roomUserRepository.save(roomUser);
        });

        return modelMapperUtil.convertToChatRoomResponse(chatRoom);
    }

    @Override
    public void updateRecentMessageData(ChatDto chatDto) {
        chatRoomRepository.updateRecentMessageData(chatDto);
    }

    @Override
    public void updateRoomUserState(RoomUserState roomUserState, ChatDto chatDto){
        chatRoomRepository.updateRoomUserState(roomUserState, chatDto);
    }

    @Override
    public void updateReadMessageCount(ChatDto chatDto) {
        chatRoomRepository.updateReadMessageCount(chatDto);
    }

    @Override
    public String findChatRoomByUserId(ChatRoomRequest chatRoomRequest) {
        List<RoomUserDto> roomUserDtoList = modelMapperUtil.convertToRoomUserDto(chatRoomRequest);
        return chatRoomRepository.findChatRoomByUserId(roomUserDtoList).getRoomUuid();
    }

    @Override
    public List<ChatRoomResponse> findAllChatRoomByUserId(String userId) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllChatRoomByUserId(userId);
        return modelMapperUtil.convertToChatRoomResponseList(chatRoomList,userId);
    }

    @Override
    public void updateEnterUserState(RoomUserState roomUserState, ChatDto chatDto) {
        chatRoomRepository.updateRoomUserState(roomUserState, chatDto);
        chatRoomRepository.updateReadMessageCount(chatDto);
    }

    @Override
    public ChatDto doExitUserProcess(StompHeaderAccessor stompHeaderAccessor) {
        String roomUUID = (String) stompHeaderAccessor.getSessionAttributes().get("roomUUID");
        String userUUID = (String) stompHeaderAccessor.getSessionAttributes().get("userUUID");
        String username = chatRoomRepository.findUsernameByRoomIdAndUserId(roomUUID,userUUID);
        ChatDto chatDtoExit = ChatDto.getInstanceExit(roomUUID, userUUID, username);
        chatRoomRepository.updateRoomUserState(RoomUserState.EXITED, chatDtoExit); // User 상태 변경하기

        return chatDtoExit;
    }

    @Override
    public String findUsername(String roomUUID, String userUUID) {
        return chatRoomRepository.findUsernameByRoomIdAndUserId(roomUUID,userUUID);
    }


    @Override
    public String deleteUserInRoom(String roomId, String userId) {
        return null;
    }
    @Override
    public void findAllRoomByUserId() {}
    @Override
    public ChatRoomDto getChatRoom(String roomId) {
        return null;
    }
}
