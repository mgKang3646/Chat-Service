package com.example.chattingservice.entity;

import com.example.chattingservice.domain.RoomUserState;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class RoomUser {

    @Id
    @GeneratedValue
    @Column(name = "room_user_id")
    private Long roomUserId;

    @Column( name = "user_uuid", nullable = false )
    private String userUuid;

    @Column( name= "user_nickname", nullable = false )
    private String userNickname;

    @Column( name="read_message_count", nullable = false )
    private int readMessageCount;

    @Column ( name = "user_image_url")
    private String userImageUrl;

    @Column ( name = "user_state")
    @Enumerated(EnumType.STRING)
    private RoomUserState userState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id")
    private ChatRoom chatRoom;

    public void updateChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }


}
