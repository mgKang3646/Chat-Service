package com.example.chattingservice;


import com.example.chattingservice.dto.ChatDto;
import com.example.chattingservice.entity.ChatMessage;
import com.example.chattingservice.repository.ChatMessageRepository;
import com.example.chattingservice.vo.RoomUserState;
import com.example.chattingservice.entity.ChatRoom;
import com.example.chattingservice.entity.RoomUser;
import com.example.chattingservice.repository.ChatRoomRepository;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.chattingservice.entity.QChatMessage.chatMessage;
import static com.example.chattingservice.entity.QChatRoom.chatRoom;
import static com.example.chattingservice.entity.QRoomUser.roomUser;

@SpringBootTest
@Transactional
@Rollback(value=false)
public class QueryDslTest {

    @Autowired
    EntityManager em;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    JPAQueryFactory queryFactory;

    private String roomUuid1;
    private String roomUuid2;
    private String roomUserId1;
    private String roomUserId2;
    private String userId1;
    private String userId2;
    private String userId3;
    private ChatRoom chatRoomEntity1;
    private ChatRoom chatRoomEntity2;



    @BeforeEach
    public void init(){

        queryFactory =  new JPAQueryFactory(em);

        roomUuid1 = UUID.randomUUID().toString();
        roomUuid2 = UUID.randomUUID().toString();

        roomUserId1 = UUID.randomUUID().toString();
        roomUserId2 = UUID.randomUUID().toString();
        userId1 = UUID.randomUUID().toString();
        userId2 = UUID.randomUUID().toString();
        userId3 = UUID.randomUUID().toString();

        chatRoomEntity1 = ChatRoom.getInstance(roomUuid1);
        chatRoomEntity1.setMessageCount(10);

        chatRoomEntity2 = ChatRoom.getInstance(roomUuid2);
        chatRoomEntity2.setMessageCount(10);

        RoomUser roomUser1 = RoomUser.builder()
                .userUuid(userId1)
                .userNickname("mingu")
                .chatRoom(chatRoomEntity1)
                .build();

        RoomUser roomUser2 = RoomUser.builder()
                .userUuid(userId2)
                .userNickname("chulsu")
                .chatRoom(chatRoomEntity1)
                .build();

        RoomUser roomUser3 = RoomUser.builder()
                .userUuid(userId1)
                .userNickname("mingu")
                .chatRoom(chatRoomEntity2)
                .build();

        RoomUser roomUser4 = RoomUser.builder()
                .userUuid(userId3)
                .userNickname("jihee")
                .chatRoom(chatRoomEntity2)
                .build();

        initMessages();

        em.persist(chatRoomEntity1);
        em.persist(chatRoomEntity2);

        em.persist(roomUser1);
        em.persist(roomUser2);
        em.persist(roomUser3);
        em.persist(roomUser4);

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
                .where(chatRoom.roomUuid.eq(roomUuid1))
                .fetchOne();
    }

    @Test
    public void updateUserState(){


        long count = queryFactory
                .update(roomUser)
                .set(roomUser.userState,RoomUserState.IN)
                .where(
                        roomUser.userUuid.eq(userId1),
                        roomUser.chatRoom.roomUuid.eq(roomUuid1)
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

        ChatRoom chatRoom1 = Optional.ofNullable(findChatRoom).orElse(ChatRoom.getInstance("20"));


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
                .roomUuid(roomUuid1)
                .build();

        long execute = queryFactory
                .update(roomUser)
                .set(roomUser.userState, RoomUserState.IN)
                .where(
                        roomUser.userUuid.eq(chatDto.getSenderUuid()),
                        roomUser.chatRoom.eq(
                                JPAExpressions.selectFrom(chatRoom)
                                        .where(chatRoom.roomUuid.eq(chatDto.getRoomUuid()))
                        )
                ).execute();

        System.out.println("execute : " + execute);
    }

    @Test
    public void selectUserStateTest(){

        ChatDto chatDto = ChatDto.builder()
                .senderUuid(userId1)
                .roomUuid(roomUuid1)
                .build();

        List<String> roomUsers = queryFactory.select(roomUser.userUuid)
                .distinct()
                .from(chatRoom)
                .join(chatRoom.roomUsers, roomUser)
                .where(
                        roomUser.userUuid.eq(chatDto.getSenderUuid()),
                        roomUser.userUuid.in(
                                JPAExpressions.select(roomUser.userUuid)
                                        .distinct()
                                        .from(chatRoom)
                                        .join(chatRoom.roomUsers,roomUser)
                                        .where( chatRoom.roomUuid.eq(roomUuid1))

                        )
                ).fetch();

        for (String user : roomUsers) {
            System.out.println("selectUserStateTest : " + user);
        }


    }

    @Test
    public void findChatRoomTest(){

        String roomUuid = queryFactory
                .select(roomUser.chatRoom.roomUuid)
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

        System.out.println(roomUuid);


    }



    @Test
    public void getChatMessageListTest(){

        PageRequest pageRequest = PageRequest.of(0,8, Sort.Direction.DESC,"messageTime");
        //Slice<ChatMessage> slicePage = chatMessageRepository.findChatMessagesByRoomUuid(roomUuid1, pageRequest);
        LocalDateTime beforeDate = LocalDateTime.of(2023,12,9,12,16,12);

        List<ChatMessage> chatMessageList = queryFactory
                .selectFrom(chatMessage)
                .where(
                        chatMessage.roomUuid.eq(roomUuid1),
                        chatMessage.messageTime.lt(beforeDate)
                ).limit(pageRequest.getPageSize() + 1)
                .orderBy(chatMessage.messageTime.asc())
                .fetch();


        System.out.println("====================Entity List======================");
        chatMessageList.forEach(chatMsg ->{System.out.println(chatMsg.getMessageTime());});

    }


//    @Test
//    public void sliceTest(){
//        long id = 4;
//        PageRequest pageRequest = PageRequest.of(0,8, Sort.Direction.DESC,"id"); // 정렬 속성 날짜로 변경하기
//        Slice<ChatMessage> sliceInstance = chatMessageRepository.findChatMessageListByRoomUuid(roomUuid1, pageRequest, id);
//        Slice<ChatDto> map = sliceInstance.map(cm -> cm.convert());
//
//
//    }
    public void initMessages(){
        chatMessageRepository.save(
                ChatMessage.builder()
                        .messageTime(LocalDateTime.of(2023,12,9,12,12,12))
                        .message("ㄷㄷㄷㄷㅌㅌㅋ!!!! ㅎㅎㅎ")
                        .senderNickname("mingu")
                        .senderUuid("1123123123123")
                        .type(ChatDto.MessageType.TALK)
                        .chatRoom(chatRoomEntity1)
                        .roomUuid(roomUuid1)
                        .build()
        );
        chatMessageRepository.save(
                ChatMessage.builder()
                        .messageTime(LocalDateTime.of(2023,12,9,12,13,12))
                        .message("반가ㅌㅌㅌㅌㅌ워!!!! ㅎㅎㅎ")
                        .senderNickname("mingu")
                        .senderUuid("1123123123123")
                        .type(ChatDto.MessageType.TALK)
                        .chatRoom(chatRoomEntity1)
                        .roomUuid(roomUuid1)
                        .build()
        );
        chatMessageRepository.save(
                ChatMessage.builder()
                        .messageTime(LocalDateTime.of(2023,12,9,12,14,12))
                        .message("반가워ㄷㄷㄷㄷ!!!! ㅎㅎㅎ")
                        .senderNickname("jihee")
                        .senderUuid("1123123132323123")
                        .type(ChatDto.MessageType.TALK)
                        .chatRoom(chatRoomEntity1)
                        .roomUuid(roomUuid1)
                        .build()
        );
        chatMessageRepository.save(
                ChatMessage.builder()
                        .messageTime(LocalDateTime.of(2023,12,9,12,15,12))
                        .message("ㅇㅇㅇㅇ!!!! ㅎㅎㅎ")
                        .senderNickname("minsu")
                        .senderUuid("11231232222123123")
                        .type(ChatDto.MessageType.TALK)
                        .chatRoom(chatRoomEntity1)
                        .roomUuid(roomUuid1)
                        .build()
        );
        chatMessageRepository.save(
                ChatMessage.builder()
                        .messageTime(LocalDateTime.of(2023,12,9,12,16,12))
                        .message("ㅋㅋㅋㅋㅋ!!!! ㅎㅎㅎ")
                        .senderNickname("minsu")
                        .senderUuid("11231232222123123")
                        .type(ChatDto.MessageType.TALK)
                        .chatRoom(chatRoomEntity1)
                        .roomUuid(roomUuid1)
                        .build()
        );
        chatMessageRepository.save(
                ChatMessage.builder()
                        .messageTime(LocalDateTime.of(2023,12,9,12,17,12))
                        .message("ㅎㅎㅎㅎㅎ!!!! ㅎㅎㅎ")
                        .senderNickname("minsu")
                        .senderUuid("11231232222123123")
                        .type(ChatDto.MessageType.TALK)
                        .chatRoom(chatRoomEntity1)
                        .roomUuid(roomUuid1)
                        .build()
        );
        chatMessageRepository.save(
                ChatMessage.builder()
                        .messageTime(LocalDateTime.of(2023,12,9,12,18,12))
                        .message("안녕!!!! ㅎㅎㅎ")
                        .senderNickname("minsu")
                        .senderUuid("11231232222123123")
                        .type(ChatDto.MessageType.TALK)
                        .chatRoom(chatRoomEntity1)
                        .roomUuid(roomUuid1)
                        .build()
        );
    }

}
