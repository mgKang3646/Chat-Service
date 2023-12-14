package com.example.chattingservice.repository.impl;

import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.repository.RoomUserRepositoryCustom;
import com.example.chattingservice.vo.RoomUserState;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static com.example.chattingservice.entity.QChatRoom.chatRoom;
import static com.example.chattingservice.entity.QRoomUser.roomUser;

public class RoomUserRepositoryImpl implements RoomUserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RoomUserRepositoryImpl(EntityManager entityManager){
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    @Transactional // 프록시로 생성된 Respository 객체는 @Transactional(ReadOnly=true)이므로 메소드 단에서 새로 정의해주어야 한다.
    public long updateRoomUserState(RoomUserState userState, ChatDto chatDto) {
        return queryFactory
                .update(roomUser)
                .set(roomUser.updatedDate, LocalDateTime.now())
                .set(roomUser.userState, userState)
                .where(
                        roomUser.userUuid.eq(chatDto.getSenderUuid()),
                        roomUser.chatRoom.eq(
                                JPAExpressions.selectFrom(chatRoom)
                                        .where(chatRoom.roomUuid.eq(chatDto.getRoomUuid()))
                        )
                ).execute();
    }

    @Override
    @Transactional
    public long updateReadMessageCount(ChatDto chatDto) {
        return queryFactory
                .update(roomUser)
                .set(roomUser.updatedDate, LocalDateTime.now())
                .set(roomUser.readMessageCount,
                        JPAExpressions.select(chatRoom.messageCount)
                                .from(chatRoom)
                                .where(chatRoom.roomUuid.eq(chatDto.getRoomUuid())))
                .where(
                        roomUser.userUuid.eq(chatDto.getSenderUuid()),
                        roomUser.chatRoom.eq(
                                JPAExpressions.selectFrom(chatRoom)
                                        .where(chatRoom.roomUuid.eq(chatDto.getRoomUuid()))
                        )
                ).execute();
    }
}
