package com.example.chattingservice;


import com.example.chattingservice.domain.ChatDto;
import com.example.chattingservice.domain.RoomUserState;
import com.example.chattingservice.entity.ChatRoom;
import com.example.chattingservice.entity.RoomUser;
import com.example.chattingservice.repository.ChatRoomRepository;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.chattingservice.entity.QChatRoom.chatRoom;
import static com.example.chattingservice.entity.QRoomUser.roomUser;

@SpringBootTest
@Transactional
@Rollback(value=false)
public class QueryDslTest {

    @Autowired
    EntityManager em;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    JPAQueryFactory queryFactory;

    private String roomUuid;
    private String roomUserId1;
    private String roomUserId2;
    private String userId1;
    private String userId2;


    @BeforeEach
    public void init(){

        queryFactory =  new JPAQueryFactory(em);

        roomUuid = UUID.randomUUID().toString();
        roomUserId1 = UUID.randomUUID().toString();
        roomUserId2 = UUID.randomUUID().toString();
        userId1 = UUID.randomUUID().toString();
        userId2 = UUID.randomUUID().toString();

        ChatRoom chatRoomEntity = new ChatRoom(roomUuid);
        chatRoomEntity.setMessageCount(10);

        RoomUser roomUser1 = RoomUser.builder()
                .userUuid(userId1)
                .userNickname("mingu")
                .chatRoom(chatRoomEntity)
                .build();

        RoomUser roomUser2 = RoomUser.builder()
                .userUuid(userId2)
                .userNickname("chulsu")
                .chatRoom(chatRoomEntity)
                .build();

        em.persist(chatRoomEntity);
        em.persist(roomUser1);
        em.persist(roomUser2);

        // QueryDSL은 JPQL을 전송한다. JPQL은 영속성컨텍스트를 거치지 않기 때문에 em.flush로 DB와 영속성 컨텍스트를 동기화 해놓아야 한다.
        // JPQL을 사용할 때는 반드시 영속성 컨텍스트와 DB를 동기화하고 영속성 컨텍스트를 클리어 한 뒤에 사용한다. !!!!
        em.flush();
        em.clear();

    }

    @Test
    public void basicTest(){

        queryFactory = new JPAQueryFactory(em);

        ChatRoom proxyChatRoom = queryFactory.selectFrom(chatRoom)
                .leftJoin(chatRoom.roomUsers)
                .fetchJoin()
                .where(chatRoom.roomUuid.eq(roomUuid))
                .fetchOne();


    }

    @Test
    public void updateUserState(){


        long count = queryFactory
                .update(roomUser)
                .set(roomUser.userState,RoomUserState.IN)
                .where(
                        roomUser.userUuid.eq(userId1),
                        roomUser.chatRoom.roomUuid.eq(roomUuid)
                ).execute();

        System.out.println("수정된 행의 개수 : " + count);

    }


//    @Test
//    public void updateMessageData(){
//        Date date = new Date();
//        ChatDto chatDto = ChatDto.builder()
//                .roomId(roomId)
//                .message("안녕 하이")
//                .time(date)
//                .build();
//
//        queryFactory
//                .update(chatRoom)
//                .set(chatRoom.recentMessage,chatDto.getMessage())
//                .set(chatRoom.recentUpdateDate,chatDto.getTime())
//                .set(chatRoom.messageCount,chatRoom.messageCount.add(1))
//                .where(
//                        chatRoom.roomId.eq(chatDto.getRoomId())
//                ).execute();
//
//
//        queryFactory
//                .update(roomUser)
//                .set(roomUser.userState,RoomUserState.IN)
//                .where(
//                        roomUser.userId.eq(userId1),
//                        roomUser.chatRoom.roomId.eq(roomId)
//                ).execute();
//
//        queryFactory
//                .update(roomUser)
//                .set(roomUser.readMessageCount, roomUser.readMessageCount.add(1))
//                .where(
//                        roomUser.chatRoom.roomId.eq(roomId),
//                        roomUser.userState.eq(RoomUserState.IN)
//                ).execute();
//
//        em.flush();
//        em.clear();
//    }

    @Test
    public void findChatRoomByUserId(){
        ChatRoom findChatRoom = queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.roomUsers, roomUser).fetchJoin()
                .where(
                        chatRoom.roomUsers.any().userUuid.eq(userId1+23233),
                        chatRoom.roomUsers.any().userUuid.eq(userId2)
                )
                .fetchOne();

        ChatRoom chatRoom1 = Optional.ofNullable(findChatRoom).orElse(new ChatRoom("20"));


        System.out.println("방 번호 : " + chatRoom1.getRoomId());
    }

    @Test
    public void findAllChatRoomByUserIdTest(){

        List<ChatRoom> chatRoomList = queryFactory.selectFrom(chatRoom)
                .distinct()
                .leftJoin(chatRoom.roomUsers, roomUser).fetchJoin()
                .where(
                        chatRoom.roomUsers.any().userUuid.eq(userId1)
                )
                .fetch();

        System.out.println("조회된 채팅방 개수 : " + chatRoomList.size());
    }

//    // find로 조회후 더티체킹으로 UPDATE 유도하기 ( 쿼리 2개 생성 )
//    @Test
//    public void updateReadMessageCountTest1(){
//        ChatRoom chatRoomEntity = queryFactory
//                .selectFrom(chatRoom)
//                .leftJoin(chatRoom.roomUsers).fetchJoin()
//                .where(chatRoom.roomId.eq())
//                .fetchOne();
//
//        chatRoomEntity.getRoomUsers().get(0).setReadMessageCount(chatRoomEntity.getMessageCount()); // 트랜잭션이 유지가 되어 수정이 된다.
//    }

    // update문으로 수정하기 ( 쿼리 하나 생성 )
//    @Test
//    public void updateReadMessageCountTest2(){
//        queryFactory
//                .update(roomUser)
//                .set(roomUser.readMessageCount,
//                        JPAExpressions.select(chatRoom.messageCount)
//                                .from(chatRoom)
//                                .where(chatRoom.roomId.eq(roomId)))
//                .where(
//                        roomUser.userId.eq(userId2),
//                        roomUser.chatRoom.roomId.eq(roomId)
//                ).execute();
//
//    }

    @Test
    public void updateUserStateTest(){

        ChatDto chatDto = ChatDto.builder()
                .senderUuid(userId1)
                .roomUuid(roomUuid)
                .build();

        long execute = queryFactory
                .update(roomUser)
                .set(roomUser.userState, RoomUserState.IN)
                .where(
                        roomUser.userUuid.eq(chatDto.getSenderUuid()),
                        roomUser.userUuid.in(
                                JPAExpressions.select(roomUser.userUuid)
                                        .from(chatRoom)
                                        .join(chatRoom.roomUsers,roomUser)
                                        .where( chatRoom.roomUuid.eq(roomUuid))

                        )
                ).execute();

        System.out.println("execute : " + execute);
    }

    @Test
    public void findUsersUuidTest(){

        List<String> userList = queryFactory.select(roomUser.userUuid)
                .from(chatRoom)
                .join(chatRoom.roomUsers, roomUser)
                .where(
                        chatRoom.roomUuid.eq(roomUuid)
                ).fetch();

        for (String s : userList) {
            System.out.println(s);
        }

    }
}
