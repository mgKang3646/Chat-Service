package com.example.chattingservice.controller;

import com.example.chattingservice.config.KafkaConfig;
import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.vo.RoomUserState;
import com.example.chattingservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatRoomService chatRoomService;

    @KafkaListener(
            topics = KafkaConfig.TOPIC_NAME,
            groupId = KafkaConfig.GROUP_ID
    )
    public void listen(ChatDto chatDto){
        log.info("===================== ChatController KAFKA LISTEN ChatDto {}", chatDto.toString());
        template.convertAndSend("/sub/chat/room/" + chatDto.getRoomUuid(), chatDto);

    }

    @MessageMapping("/chat/enterUser")
    public void enterChatRoom(@Payload ChatDto chatDto, SimpMessageHeaderAccessor headerAccessor){

        log.info( " ===================== ENTER ChatDto : {} ", chatDto.toString());
        // STOMP 세션에 userId와 roomId 저장하기 ( 세션 종료 이벤트 발생시, Room 객체에서 User 제거 )
        headerAccessor.getSessionAttributes().put("userUUID", chatDto.getSenderUuid());
        headerAccessor.getSessionAttributes().put("roomUUID", chatDto.getRoomUuid());

        chatRoomService.updateEnterUserState(RoomUserState.IN,chatDto);
        template.convertAndSend("/sub/chat/room/" + chatDto.getRoomUuid(), ChatDto.getInstanceEnter(chatDto));

    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDto chatDto) {
        chatRoomService.updateRecentMessageData(chatDto); // 채팅방 정보 update
        template.convertAndSend("/sub/chat/room/" + chatDto.getRoomUuid(), chatDto);
    }

    @MessageMapping("/chat/leave")
    public void leaveChatRoom(@Payload ChatDto chatDto){
        chatRoomService.updateRoomUserState(RoomUserState.LEAVE,chatDto);
    }

    // 세션이 종료된 경우
    @EventListener
    public void webSocketDisconnectionListener(SessionDisconnectEvent event){
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        ChatDto chatDtoExit = chatRoomService.doExitUserProcess(stompHeaderAccessor);
        template.convertAndSend("/sub/chat/room/" + chatDtoExit.getRoomUuid(), chatDtoExit);
    }

    // 유저 조회

    // 유저 닉네임 중복 조회

}
