
import React, { useEffect, useState, useRef } from 'react'
import './ChatPage.css'
import { useLocation, useNavigate } from 'react-router-dom'
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import axios from 'axios';

function Chat(){

    const location = useLocation();
    const stompUrl = 'http://192.168.219.101:8000/chatting-service/ws-stomp';
    const roomId = location.state?.roomId;
    const ownerName = location.state?.ownerName;
    const stompClient = useRef(null);
    const [messages, setMessages] = useState([]); // 상태를 관리하는 빈 배열 생성 
    const [input, setInput] = useState('');
    const navigate = useNavigate();

    const connect = () => {
        let socket = new SockJS(stompUrl);
        stompClient.current = Stomp.over(socket);
        stompClient.current.connect({}, onConnected, onError);

        // let jwtHeader = {
        //   'Authorization' : jwt // 헤더 추가가 안됨..... 나중에 ...
        // };

    }

    useEffect(()=>{
        connect();

    },[]); 

    const handleInputChange= (event) =>{
        setInput(event.target.value);
    }

    // 요청을 3번 보낸다 ( 1. 구독, 2. 세션셜정 3.입장 메시지 ) => 요청 횟수를 줄일 수 있는 방법은 없을까?
    function onConnected() {
        
        stompClient.current.subscribe('/sub/chat/room/' + roomId, onMessageReceived,{'id':roomId}); // 구독 요청하기

        let sendMsgForm = {
            roomUuid: roomId,
            senderNickname: localStorage.getItem('username'),
            senderUuid: localStorage.getItem('userId'),
            type: 'ENTER'
        };

        setSessionOptions(sendMsgForm); // 세션설정정보 전송하기 
        sendEnterMessage(sendMsgForm); // 입장 메시지 전송하기
        
    }

    function setSessionOptions(sendMsgForm){
        stompClient.current.send("/pub/chat/sessions",{},JSON.stringify(sendMsgForm))
    }

    const sendEnterMessage = async(sendMsgForm) => {

        const response = await axios({
            method: "post",
            url: '/chatting-service/send',
            data: sendMsgForm,
            headers: { "Content-Type" : "application/json"}
        });
    }

    // 채팅방에 메시지 전송하기 
    const sendMessage = async(event)=>{
        
        const chatMsg = input.trim();
        const currentTime = getCurrentTime();

        let sendMsgForm = {
            roomUuid: roomId,
            senderNickname: localStorage.getItem('username'),
            senderUuid: localStorage.getItem('userId'),
            message: chatMsg,
            messageTime: currentTime,
            type: 'TALK'
        };

        const response = await axios({
            method: "post",
            url: '/chatting-service/send',
            data: sendMsgForm,
            headers: { "Content-Type" : "application/json"}
        });

        event.preventDefault();
    }

    //  // Kafka 적용 전 sendMessage
    // function sendMessageBeforeKafka(event) {
    

    //     const chatMsg = input.trim();
    //     const currentTime = getCurrentTime();

    //     console.log("채팅 전송 시작 : " + chatMsg);
    //     console.log("채팅 클라이언트 : " + stompClient.current);
    //     if (chatMsg && stompClient.current) {

    //         let sendMsgForm = {
    //             roomUuid: roomId,
    //             senderNickname: localStorage.getItem('username'),
    //             senderUuid: localStorage.getItem('userId'),
    //             message: chatMsg,
    //             messageTime: currentTime,
    //             type: 'TALK'
    //         };

    //         stompClient.current.send("/pub/chat/sendMessage", {}, JSON.stringify(sendMsgForm));
    //     }
    //     event.preventDefault();
    // }

    
    // 채팅방 벗어나기 ( 채팅방 화면에서 나가기 )
    function leaveChatRoom(event) {
        if(stompClient.current){

            let leaveMsgForm = {
                roomUuid: roomId,
                senderNickname: localStorage.getItem('username'),
                senderUuid: localStorage.getItem('userId'),
                type: 'LEAVE'
            };

            stompClient.current.send("/pub/chat/leave",{},JSON.stringify(leaveMsgForm));

        }
        event.preventDefault();
        navigate("/chatlist");
    }

    // 채팅방 퇴장하기 
    function exitChatRoom(event){
        stompClient.current.unsubscribe(roomId); // 채팅방 구독 취소하기
        stompClient.current.disconnect(); // STOMP 세션 연결 끊기 
        navigate('/chatlist');
        event.preventDefault();
    }

    function onError(error) {
        console.log("Could not conenct to WebSocker Server..");
    }
    
    // 메시지를 받을 때도 마찬가지로 JSON 타입으로 받으며,
    // 넘어온 JSON 형식의 메시지를 parse 해서 사용한다.
    function onMessageReceived(payload) {
        let chat = JSON.parse(payload.body);
        console.log(chat)

        if (chat.type === 'ENTER') {  // chatType 이 enter 라면 아래 내용
            setMessages(messages=>[...messages,chat.message]);

        } else if (chat.type === 'LEAVE') { // chatType 가 leave 라면 아래 내용
            chat.content = chat.sender + chat.message;


        } else { // chatType 이 talk 라면 아래 내용
            setMessages(messages=>[...messages, chat.message]);
        }
    }

    const getCurrentTime = ()=>{
        const now = new Date();
        const year = now.getFullYear();
        const month = now.getMonth() + 1; // 월은 0부터 시작하므로 1을 더합니다.
        const date = now.getDate();
        const hours = now.getHours();
        const minutes = now.getMinutes();
        const formattedDateTime = `${year}-${month.toString().padStart(2, '0')}-${date.toString().padStart(2, '0')} ${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;

        console.log("현재시간 : " + formattedDateTime);
        
        return formattedDateTime;
    }



    return (
        <div className="chat">
            <div className="chat-window">
                <div className='chat-header'>
                    <button className='round-button' onClick={leaveChatRoom}> 벗어나기 </button>
                    <h2>{ownerName}</h2>
                    <button className='round-button' onClick={exitChatRoom}> 나가기 </button>
                </div>
                <div className="messages">
                    {messages.map((message, index) => (
                        <div key={index} className="message">
                            {message}
                        </div>
                    ))}
                </div>
                <div className="input-area">
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