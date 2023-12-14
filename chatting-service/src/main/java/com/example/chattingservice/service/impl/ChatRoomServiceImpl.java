package com.example.chattingservice.service.impl;


import com.example.chattingservice.config.properties.vo.PageConfigVo;
import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.dto.RoomUserFindDto;
import com.example.chattingservice.entity.ChatMessage;
import com.example.chattingservice.entity.ChatRoom;
import com.example.chattingservice.entity.RoomUser;
import com.example.chattingservice.repository.ChatMessageRepository;
import com.example.chattingservice.repository.ChatRoomRepository;
import com.example.chattingservice.repository.RoomUserRepository;
import com.example.chattingservice.service.ChatRoomService;
import com.example.chattingservice.util.ModelMapperUtil;
import com.example.chattingservice.vo.ChatRoomResponse;
import com.example.chattingservice.vo.RoomUserState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ModelMapperUtil modelMapperUtil;
    private final ChatRoomRepository chatRoomRepository;
    private final RoomUserRepository roomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PageConfigVo pageConfigVo;


    @Override
    public String findOrCreateChatRoom(String userUuid, String targetUuid) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByUserId(RoomUserFindDto.getInstance(userUuid, targetUuid));
//        chatRoom = Optional.ofNullable(chatRoom).orElse(createChatRoom(userUuid, targetUuid));
        chatRoom = Optional.ofNullable(chatRoom).orElseGet(()->createChatRoom(userUuid, targetUuid));
        return chatRoom.getRoomUuid();
    }

    @Override
    public void processSendMessage(ChatDto chatDto) {
        chatRoomRepository.updateRecentMessageData(chatDto); // ChatRoom 데이터 update ( ChatMessage 엔티티 생성으로 인해 쿼리 조회로 가능해질 듯 => 리팩토링 필요 )
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByRoomUuid(chatDto.getRoomUuid());  // 1. roomUuid chatRoom 조회
        ChatMessage chatMessage = modelMapperUtil.convertToChatMessage(chatDto,chatRoom);// 2. ChatDto -> ChatMessage로 변환 후 chatRoom 세팅
        chatMessageRepository.save(chatMessage);// 3. save
    }

    @Override
    public void updateRoomUserState(RoomUserState roomUserState, ChatDto chatDto){
        roomUserRepository.updateRoomUserState(roomUserState, chatDto);
    }

    @Override
    public List<ChatRoomResponse> findAllChatRoomByUserId(String userId) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllChatRoomByUserId(userId);
        return modelMapperUtil.convertToChatRoomResponseList(chatRoomList,userId);
    }

    @Override
    public void updateEnterUserState(RoomUserState roomUserState, ChatDto chatDto) {
        roomUserRepository.updateRoomUserState(roomUserState, chatDto);
        roomUserRepository.updateReadMessageCount(chatDto);
    }

    @Override
    public ChatDto doExitUserProcess(StompHeaderAccessor stompHeaderAccessor) {
        String roomUUID = (String) stompHeaderAccessor.getSessionAttributes().get("roomUUID");
        String userUUID = (String) stompHeaderAccessor.getSessionAttributes().get("userUUID");
        ChatDto chatDtoExit = ChatDto.getInstanceExit(roomUUID, userUUID);
        roomUserRepository.updateRoomUserState(RoomUserState.EXITED, chatDtoExit); // User 상태 변경하기

        return chatDtoExit;
    }

    @Override
    public Slice<ChatDto> findAllChatList(String roomUuid, LocalDateTime beforeTime) {
        PageRequest pageRequest = PageRequest.of(pageConfigVo.getOffset(),pageConfigVo.getSize(),
                Sort.Direction.DESC,pageConfigVo.getOrderBy());
        Slice<ChatMessage> chatSliceMessages =
                chatMessageRepository.findChatMessageListByRoomUuid(roomUuid, pageRequest, beforeTime);
        return chatSliceMessages.map(chatMessage -> chatMessage.convert());
    }

    private String findChatRoomUuID(RoomUserFindDto roomUserFindDto) {
        return chatRoomRepository.findChatRoomByUserId(roomUserFindDto).getRoomUuid();
    }

    private ChatRoom createChatRoom(String userUuid, String targetUuid) {
        ChatRoom chatRoom = ChatRoom.getInstance();
        RoomUser user = modelMapperUtil.convertToRoomUser(userUuid);
        RoomUser target = modelMapperUtil.convertToRoomUser(targetUuid);

        user.updateChatRoom(chatRoom);
        target.updateChatRoom(chatRoom);
        chatRoomRepository.save(chatRoom);
        roomUserRepository.save(user);
        roomUserRepository.save(target);

        return chatRoom;
    }



}
