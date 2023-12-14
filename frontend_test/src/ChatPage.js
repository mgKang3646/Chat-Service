
import React, { useState, useRef } from 'react'
import './ChatPage.css'
import { useNavigate } from 'react-router-dom'
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import axios from 'axios';

function Chat(){

    const stompUrl = 'http://35.230.38.162/ws-stomp';
    const roomUuid = "3d429ce3-5795-4dd5-b36b-528ef74037c3";
    const userUuid = "11111111";
    const targetUuid = "22222222";

    const stompClient = useRef(null);
    const [chatList, setChatList] = useState([]);
    const [input, setInput] = useState('');
    const navigate = useNavigate();


    // StompClient 연결
    const connect = () => {
        const socket = new SockJS(stompUrl);
        stompClient.current = Stomp.over(socket);
        stompClient.current.connect({}, onConnected, onError);
    }

    function onConnected() { 
        // 1. 구독 요청 전송
        stompClient.current.subscribe('/sub/chat/room/' + roomUuid, onMessageReceived,{'id':roomUuid}); 

        let sendMsgForm = {
            roomUuid: roomUuid,
            senderUuid: userUuid,
            messageTime: getKoreanTimeISO(),
            type: 'ENTER'
        };

        // kafka를 붙이게 되면서 요청을 2개로 나누어 전송하게 됨.
        // 2. 세션정보 설정 요청 전송
        sendSessioninfo(sendMsgForm);
        // 3. 입장 메시지 전송
        sendEnterMessage(sendMsgForm); 
    }

    // 세션정보 전송하기
    function sendSessioninfo(sendMsgForm){
        stompClient.current.send("/pub/chat/sessions",{},JSON.stringify(sendMsgForm))
    }

    // 입장 메시지 전송하기
    function sendEnterMessage(sendMsgForm) {
        if (stompClient.current) {
            stompClient.current.send("/pub/send", {}, JSON.stringify(sendMsgForm));
        }
    }

    // 채팅 메시지 전송하기 
    function sendMessage(event) {
        const chatMsg = input.trim();
        console.log("현재시간 : "+ getKoreanTimeISO())
        let sendMsgForm = {
            roomUuid: roomUuid,
            senderUuid: userUuid,
            message: chatMsg,
            messageTime: getKoreanTimeISO(),
            type: 'TALK'
        };

        if (chatMsg && stompClient.current) {
            stompClient.current.send("/pub/send", {}, JSON.stringify(sendMsgForm));
        }

        event.preventDefault();
    }
    
    // 채팅방 벗어나기( 채팅방 화면에서 나가기 )
    function leaveChatRoom(event) {
        if(stompClient.current){

            let leaveMsgForm = {
                roomUuid: roomUuid,
                senderUuid: userUuid,
                type: 'LEAVE'
            };

            stompClient.current.send("/pub/chat/leave",{},JSON.stringify(leaveMsgForm));

        }
        navigate("/chatlist"); // 화면전환
        event.preventDefault();
    }

    // 채팅방 퇴장하기 
    function exitChatRoom(event){
        stompClient.current.unsubscribe(roomUuid); // 채팅방 구독 취소하기
        stompClient.current.disconnect(); // STOMP 세션 연결 끊기 
        navigate('/chatlist'); // 화면전환
        event.preventDefault();
    }

    function onError(error) {
        console.log("Could not conenct to WebSocker Server..");
    }
    
    // STOMP 콜백함수
    function onMessageReceived(payload) {
        let chat = JSON.parse(payload.body);
        console.log(chat)

        if (chat.type === 'ENTER') {  
            setChatList(chatList=>[...chatList,chat]);

        } else if (chat.type === 'LEAVE') {} 
        else { 
            setChatList(chatList=>[...chatList, chat]);
        }
    }

    // 현재시간 
    function getKoreanTimeISO() {
        const now = new Date();
        const koreanTime = new Date(now.getTime() + (9 * 60 * 60 * 1000));
        return koreanTime.toISOString();
      }
    

    const handleInputChange= (event) =>{
        setInput(event.target.value);
    }

    const createRoom = async(event) => {

        let url = '/api/chat/createroom?targetUuid='+targetUuid;
        const response = await axios.get(url,{
            headers: {
                userUuid: userUuid
            }
        });

        console.log("요청!")
        if(response.status === 201){
            console.log(response);
        }

        event.preventDefault();

    }

    return (
        <div className="chat">

            <div className="chat-window">
                <div className='chat-header'>
                    <button className='round-button' onClick={createRoom}> 채팅방 생성하기 </button>
                    <button className='round-button' onClick={leaveChatRoom}> 벗어나기 </button>
                    <button className='round-button' onClick={exitChatRoom}> 나가기 </button>
                </div>
                <div className="chatList">
                    {chatList.map((chat, index) => (
                        <div key={index} className="message">
                            {chat.message}
                        </div>
                    ))}
                </div>
                <div className="input-area">
                    <button onClick={connect}> 연결하기 </button>
                    <input
                        type="text"
                        value={input}
                        onChange={handleInputChange}
                        placeholder='채팅을 입력해주세요'
                    />
                    <button onClick={sendMessage}> 전송 </button>

                </div>
            </div>
        </div>
    );
    }

export default Chat;