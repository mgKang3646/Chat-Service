package com.example.chattingservice.entity;


import com.example.chattingservice.dto.ChatDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue
    @Column(name = "message_id")
    private Long id;
    @Column(name="message")
    private String message;
    @Column(name="message_time")
    private String messageTime;
    @Column(name="sender_uuid")
    private String senderUuid;
    @Column(name="sender_nickname")
    private String senderNickname; // 임시로 생성
    @Column(name="type")
    @Enumerated(EnumType.STRING)
    private ChatDto.MessageType type;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    public void updateChatRoom(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }
}
