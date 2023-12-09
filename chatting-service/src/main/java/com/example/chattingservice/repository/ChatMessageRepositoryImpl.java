package com.example.chattingservice.repository;

import com.example.chattingservice.entity.ChatMessage;
import org.springframework.data.domain.Slice;

public class ChatMessageRepositoryImpl implements ChatMessageRepositoryCustom{
    @Override
    public Slice<ChatMessage> findChatMessageListByRoomUuid() {
        return null;
    }
}
