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
import com.example.chattingservice.vo.ChatRoomCreateRequest;
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

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ModelMapperUtil modelMapperUtil;
    private final ChatRoomRepository chatRoomRepository;
    private final RoomUserRepository roomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PageConfigVo pageConfigVo;

    @Override
    public ChatRoomResponse createChatRoom(ChatRoomCreateRequest chatRoomCreateRequest) {
        ChatRoom chatRoom = modelMapperUtil.convertToChatRoom(chatRoomCreateRequest);
        List<RoomUser> roomUserList = modelMapperUtil.convertToRoomUser(chatRoomCreateRequest);

        // 영속화 => QueryDSL Insert문으로 변경하여 코드 줄이기
        chatRoomRepository.save(chatRoom);
        roomUserList.forEach(roomUser -> {
            roomUser.updateChatRoom(chatRoom);
            roomUserRepository.save(roomUser);
        });

        return modelMapperUtil.convertToChatRoomResponse(chatRoom);
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
        chatRoomRepository.updateRoomUserState(roomUserState, chatDto);
    }

    @Override
    public void updateReadMessageCount(ChatDto chatDto) {
        chatRoomRepository.updateReadMessageCount(chatDto);
    }

    @Override
    public ChatRoomResponse findOrCreateChatRoom(ChatRoomCreateRequest chatRoomCreateRequest) {
        String roomUuid = findChatRoomUuID(RoomUserFindDto.convert(chatRoomCreateRequest));
        return (roomUuid.equals("0")) ? createChatRoom(chatRoomCreateRequest) : ChatRoomResponse.getInstance(roomUuid);
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


}
