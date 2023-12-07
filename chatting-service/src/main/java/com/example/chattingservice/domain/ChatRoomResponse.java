package com.example.chattingservice.domain;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor( access = AccessLevel.PROTECTED)
public class ChatRoomResponse {

    private String roomUuid;
    private String targetUuid;
    private String targetNickname;
    private String recentMessage;
    private String recentDate;
    private int messageNotRead;


    public ChatRoomResponse(String roomUuid) {
        this.roomUuid = roomUuid;
    }

    public static ChatRoomResponse convert(ChatRoomDto chatRoomDto, String sender){
        ChatRoomResponse chatRoomResponse = new ChatRoomResponse();
        for (RoomUserDto roomUser : chatRoomDto.getRoomUsers()) {
            if(!roomUser.getUserUuid().equals(sender)) {
                chatRoomResponse.setRoomUuid(chatRoomDto.getRoomUuid()); // RoomId
                chatRoomResponse.setTargetNickname(roomUser.getUserNickname()); // 대화상대 닉네임
                chatRoomResponse.setTargetUuid(roomUser.getUserUuid()); // 대화상대 userId
                chatRoomResponse.setRecentMessage(chatRoomDto.getRecentMessage()); // 가장 최근 대화 메시지
                chatRoomResponse.setRecentDate(chatRoomDto.getRecentDate()); // 가장 최근 대화 시간
            }else{ // 자기 자신인 경우
                chatRoomResponse.setMessageNotRead((chatRoomDto.getMessageCount() - roomUser.getReadMessageCount())); // 안 읽은 메시지 개수
            }
        }

        return chatRoomResponse;
    }


}
