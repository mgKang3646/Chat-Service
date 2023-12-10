import React, { useEffect , useState } from 'react'
import axios from 'axios';
import './ChatListPage.css'
import { useNavigate } from 'react-router-dom'


function ChatListPage(){

    const [chatList, setChatList ] = useState([]);
    const navigate = useNavigate();

    const getChatList = async() =>{
        const jwt = localStorage.getItem('jwt');

        // 토큰이 없는 경우
        if(jwt === null) {
            alert("JWT 토큰이 없습니다. 로그인을 먼저 시도해주세요.");
            navigate('/login');
        }

        else{
            try{
                const userUuid = localStorage.getItem('userId'); 

                console.log("현재 UUID : " + userUuid)
                const url = '/chatting-service/api/chat/rooms'
                const response = await axios.get(url,{
                    headers: {
                        Authorization: jwt,
                        userUuid: userUuid
                    }
                });

                if(response.status === 200){
                    console.log(response.data);
                    setChatList(response.data);
                }
            }catch(error){
                console.log(error);
            }
        }

    }

    function goChatPage(event,chat) {

        navigate('/chatPage',{
            state: {
                roomId: chat.roomUuid,
                ownerName: chat.targetNickname
            }
        });

        event.preventDefault();
    }

    useEffect(()=>{
        getChatList();
    },[]);


    return (
    <div class="chat-list">
        {chatList.map((chat, index) => (
            <div class="chat-room" key={index} onClick={(event)=>goChatPage(event,chat)}>
                <div class="chat-info">
                    <div class="receiver-name">{chat.targetNickname}</div>
                    <div class="recent-message">{chat.recentMessage}</div>
                    <div class="message-count">{chat.messageNotRead}</div>
                    <div class="update-time">{chat.recentDate}</div>
                </div>
            </div>
        ))}
    </div>

    );
}

export default ChatListPage;
