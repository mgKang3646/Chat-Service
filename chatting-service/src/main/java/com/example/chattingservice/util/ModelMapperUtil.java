package com.example.chattingservice.util;


import com.example.chattingservice.domain.ChatRoomDto;
import com.example.chattingservice.domain.ChatRoomRequest;
import com.example.chattingservice.domain.ChatRoomResponse;
import com.example.chattingservice.domain.RoomUserDto;
import com.example.chattingservice.entity.ChatRoom;
import com.example.chattingservice.entity.RoomUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ModelMapperUtil {

    private final ModelMapper mapper;

    public List<RoomUserDto> convertToRoomUserDto(ChatRoomRequest chatRoomRequest){
        List<RoomUserDto> roomUserDtoList = new ArrayList<>();
        roomUserDtoList.add(RoomUserDto.getSenderInstance(chatRoomRequest));
        roomUserDtoList.add(RoomUserDto.getReceiverInstance(chatRoomRequest));

        return roomUserDtoList;
    }

    public ChatRoom convertToChatRoom(ChatRoomRequest request){
        return convertChatRoomDtoToChatRoom(ChatRoomDto.getInstance());
    }

    public List<RoomUser> convertToRoomUser(ChatRoomRequest chatRoomRequest){

        return convertRoomUserDtoListToRoomUserList(convertToRoomUserDto(chatRoomRequest));
    }
    public ChatRoomResponse convertToChatRoomResponse(ChatRoom chatRoom){
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(chatRoom,ChatRoomResponse.class);
    }

    public List<ChatRoomResponse> convertToChatRoomResponseList(List<ChatRoom> chatRoomList,String sender){
        return convertChatRoomDtoListToChatRoomResponseList(convertChatRoomListToChatRoomDtoList(chatRoomList),sender);
    }
    private ChatRoom convertChatRoomDtoToChatRoom(ChatRoomDto chatRoomDto){
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ChatRoom chatRoom = mapper.map(chatRoomDto, ChatRoom.class);
        return chatRoom;
    }

    private List<RoomUser> convertRoomUserDtoListToRoomUserList(List<RoomUserDto> roomUserDtoList ){

        List<RoomUser> roomUsers = new ArrayList<>();
        roomUserDtoList.forEach(roomUserDto -> {
            RoomUser roomUser = mapper.map(roomUserDto, RoomUser.class);
            roomUsers.add(roomUser);
        });

        return roomUsers;
    }

    private List<ChatRoomDto> convertChatRoomListToChatRoomDtoList(List<ChatRoom> chatRoomList){
        List<ChatRoomDto> chatRoomDtoList = new ArrayList<>();
        chatRoomList.forEach( chatRoom -> {
            ChatRoomDto chatRoomDto = mapper.map(chatRoom, ChatRoomDto.class);
            chatRoomDtoList.add(chatRoomDto);
        });

        return chatRoomDtoList;
    }

    private List<ChatRoomResponse> convertChatRoomDtoListToChatRoomResponseList(List<ChatRoomDto> chatRoomDtoList, String sender){

        List<ChatRoomResponse> chatRoomResponseList = new ArrayList<>();
        chatRoomDtoList.forEach(chatRoomDto -> {
            chatRoomResponseList.add(ChatRoomResponse.convert(chatRoomDto,sender));
        });

        return chatRoomResponseList;
    }

}
