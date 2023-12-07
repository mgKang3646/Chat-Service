package com.example.chattingservice;


import com.example.chattingservice.domain.ChatRoomDto;
import com.example.chattingservice.domain.RoomUserDto;
import com.example.chattingservice.entity.ChatRoom;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@SpringBootTest
public class MapperTest {




//    @Test
//    public void convertDtoToEntityTest(){
//        ModelMapper modelMapper = new ModelMapper();
//        Date date = new Date();
//        long currentTime = date.getTime();
//
//        RoomUserDto roomUserDto1 = RoomUserDto.builder()
//                .userId(UUID.randomUUID().toString())
//                .username("userA")
//                .isParticipating(true)
//                .build();
//
//        RoomUserDto roomUserDto2 = RoomUserDto.builder()
//                .userId(UUID.randomUUID().toString())
//                .username("userB")
//                .isParticipating(true)
//                .build();
//
//        List<RoomUserDto> roomUserDtos = new ArrayList<>();
//        roomUserDtos.add(roomUserDto1);
//        roomUserDtos.add(roomUserDto2);
//
//
//        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
//                .roomId(UUID.randomUUID().toString())
//                .recentMessage("안녕하세요")
//                .recentUpdateDate(date)
//                .roomUsers(roomUserDtos)
//                .build();
//
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        ChatRoom chatRoom = modelMapper.map(chatRoomDto, ChatRoom.class);
//        System.out.println(chatRoom);
//
//    }
}
