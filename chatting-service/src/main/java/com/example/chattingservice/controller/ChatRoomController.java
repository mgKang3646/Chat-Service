package com.example.chattingservice.controller;
import com.example.chattingservice.config.KafkaConfig;
import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.vo.ChatRoomRequest;
import com.example.chattingservice.vo.ChatRoomResponse;
import com.example.chattingservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequiredArgsConstructor
//@CrossOrigin
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final KafkaTemplate<String, ChatDto> kafkaTemplate;

    @PostMapping("/kafka/publish")
    public void testSendMessage(@RequestBody ChatDto chatDto) throws ExecutionException, InterruptedException {
        if(chatDto.getType()==ChatDto.MessageType.TALK) chatRoomService.updateRecentMessageData(chatDto);
        else if(chatDto.getType()==ChatDto.MessageType.ENTER) chatDto.updateEnterMessage();

        kafkaTemplate.send(KafkaConfig.TOPIC_NAME,chatDto).get();
    }

    // 채팅리스트 화면 조회
    @GetMapping("/{userId}/rooms")
    public List<ChatRoomResponse> findAllChatRoomsByUserId(@PathVariable String userId){
        return chatRoomService.findAllChatRoomByUserId(userId);
    }

    // 채팅방 생성
    @PostMapping("/createroom")
    public ChatRoomResponse createRoom(@RequestBody ChatRoomRequest chatRoomRequest) {
        log.info("Get ChatRoomRequest {}", chatRoomRequest);

        // 기존 대화방이 존재하는지 조회하기
        String findRoomId = chatRoomService.findChatRoomByUserId(chatRoomRequest);

        // 기존 대화방이 존재하지 않으면 새로운 방 생성
        if(findRoomId.equals("0")) {
            return chatRoomService.createChatRoom(chatRoomRequest);
        }
        // 기존 대화방이 존재하면 기존 방 반환
        else{
            return new ChatRoomResponse(findRoomId);
        }
    }


}
