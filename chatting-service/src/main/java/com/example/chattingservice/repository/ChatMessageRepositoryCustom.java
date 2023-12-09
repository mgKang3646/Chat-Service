package com.example.chattingservice.repository;

import com.example.chattingservice.entity.ChatMessage;
import org.springframework.data.domain.Slice;

public interface ChatMessageRepositoryCustom {

    public Slice<ChatMessage> findChatMessageListByRoomUuid();
}
