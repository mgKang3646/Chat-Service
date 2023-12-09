package com.example.chattingservice.repository;

import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.dto.RoomUserDto;
import com.example.chattingservice.vo.RoomUserState;
import com.example.chattingservice.entity.ChatRoom;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static com.example.chattingservice.entity.QChatRoom.chatRoom;
import static com.example.chattingservice.entity.QRoomUser.roomUser;

//where 조건절 메소드로 만들어 분리하기
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ChatRoomRepositoryImpl(EntityManager entityManager){
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public ChatRoom findChatRoomWithRoomUserByRoomId(String roomUuid) {
        return queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.roomUsers).fetchJoin()
                .where(chatRoom.roomUuid.eq(roomUuid))
                .fetchOne();
    }

    @Override
    public String findUsernameByRoomIdAndUserId(String roomUuid,String userUuid) {

        return queryFactory.select(roomUser.userNickname)
                .from(roomUser)
                .where(
                        roomUser.userUuid.eq(userUuid),
                        roomUser.chatRoom.roomUuid.eq(roomUuid)
                ).fetchOne();
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
    public long updateRecentMessageData(ChatDto chatDto) {

        // ChatRoom Entity 메시지 데이터 수정하기
        queryFactory
                .update(chatRoom)
                .set(chatRoom.updatedDate, LocalDateTime.now())
                .set(chatRoom.recentMessage,chatDto.getMessage())
                .set(chatRoom.recentDate,chatDto.getMessageTime())
                .set(chatRoom.messageCount,chatRoom.messageCount.add(1))
                .where(
                        chatRoom.roomUuid.eq(chatDto.getRoomUuid())
                ).execute();

        // RoomUser Entity 메시지 데이터 수정하기
        queryFactory
                .update(roomUser)
                .set(roomUser.updatedDate, LocalDateTime.now())
                .set(roomUser.readMessageCount, roomUser.readMessageCount.add(1))
                .where(
                        roomUser.userState.eq(RoomUserState.IN),
                        roomUser.chatRoom.eq(
                                JPAExpressions.selectFrom(chatRoom)
                                        .where(chatRoom.roomUuid.eq(chatDto.getRoomUuid()))
                        )
                ).execute();



        return 1; // 임시로 return
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

    @Override
    public ChatRoom findChatRoomByUserId(List<RoomUserDto> roomUserDtoList) {

        String userId1 = roomUserDtoList.get(0).getUserUuid();
        String userId2 = roomUserDtoList.get(1).getUserUuid();

        ChatRoom findChatRoom = queryFactory
                .select(roomUser.chatRoom)
                .from(roomUser)
                .leftJoin(roomUser.chatRoom, chatRoom)
                .where(
                        roomUser.userUuid.eq(userId1),
                        roomUser.chatRoom.roomUuid.in(
                                JPAExpressions
                                        .select(roomUser.chatRoom.roomUuid)
                                        .from(roomUser)
                                        .leftJoin(roomUser.chatRoom, chatRoom)
                                        .where(
                                                roomUser.userUuid.eq(userId2)
                                        )
                        )
                ).fetchOne();

        return Optional.ofNullable(findChatRoom).orElse(ChatRoom.getInstance("0"));

    }

    @Override
    public List<ChatRoom> findAllChatRoomByUserId(String userUuid) {


        return queryFactory.selectFrom(chatRoom)
                .distinct()
                .leftJoin(chatRoom.roomUsers, roomUser)
                .fetchJoin()
                .where(chatRoom.roomId.in(JPAExpressions.select(roomUser.chatRoom.roomId)
                        .from(roomUser)
                        .where(
                                roomUser.userState.ne(RoomUserState.EXITED),
                                roomUser.userUuid.eq(userUuid)
                        )))
                .fetch();
    }




}
