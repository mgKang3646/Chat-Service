package com.example.chattingservice.controller;
import com.example.chattingservice.config.KafkaConfig;
import com.example.chattingservice.config.properties.vo.KafkaConfigVo;
import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.vo.ChatRoomCreateRequest;
import com.example.chattingservice.vo.ChatRoomResponse;
import com.example.chattingservice.service.ChatRoomService;
import com.example.chattingservice.vo.ErrorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
//@CrossOrigin
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final KafkaTemplate<String, ChatDto> kafkaTemplate;
    private final KafkaConfigVo kafkaConfigVo;

    @PostMapping("/send")
    public void publishMessageToTopic(@RequestBody @Valid ChatDto chatDto)
            throws ExecutionException, InterruptedException {
        if(chatDto.getType()==ChatDto.MessageType.TALK) chatRoomService.processSendMessage(chatDto);
        else if(chatDto.getType()==ChatDto.MessageType.ENTER) chatDto.updateEnterMessage();

        kafkaTemplate.send(kafkaConfigVo.getTopicName(),chatDto).get();
    }

    // 채팅리스트 화면 조회
    @GetMapping("/api/chat/rooms")
    public List<ChatRoomResponse> findAllChatRoomsByUserId(@RequestHeader("userUuid") @NotBlank String userUuid){
        return chatRoomService.findAllChatRoomByUserId(userUuid);
    }

    // 채팅방 생성
    @PostMapping("/api/chat/createroom")
    public ChatRoomResponse createRoom(@RequestBody @Valid ChatRoomCreateRequest chatRoomCreateRequest) {
        return chatRoomService.findOrCreateChatRoom(chatRoomCreateRequest);
    }

    @GetMapping("/api/chat")
    public Slice<ChatDto> findAllChatList(@RequestParam("roomUuid") @NotBlank String roomUuid,
                                          @RequestParam("beforeTime") @NotBlank String beforeTime ){
        return chatRoomService.findAllChatList(roomUuid,
                LocalDateTime.parse(beforeTime, DateTimeFormatter.ISO_DATE_TIME));
    }



}
