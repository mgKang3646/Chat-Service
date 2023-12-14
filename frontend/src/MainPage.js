import React, { useState, useLayoutEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './MainPage.css'

// http://192.168.219.101:8000/user-service/users
function MainPage(){
    const [users, setUsers] = useState([]);    
    const navigate = useNavigate();
    const getUserList = async() =>{
        const jwt = localStorage.getItem('jwt');

        //토큰이 없는 경우
        if(jwt === null) {
            alert("JWT 토큰이 없습니다. 로그인을 먼저 시도해주세요.");
            navigate('/login');
        }
        else{
            try{
                const response = await axios.get(`/user-service/users`,{
                    headers: {Authorization: jwt}
                });
                if(response.status === 200){
                    setUsers(response.data.content); // 배열(array) 데이터를 useState 베열 변수에 저장
                }
            }catch(error){
                   console.log(error);
            }
        }
    }
    useLayoutEffect(() =>{
        getUserList();
    },[]); // 의존성 배열에 있는 값들의 변경을 감지하여 훅안의 코드를 재실행 한다. 

    const createChatRoom = async(event, user) =>{
        event.preventDefault();
        const url = "chatting-service/api/chat/createroom?targetUuid=" + user.userId;

        try{

            const response = await axios.get(url,{
                headers: {
                    userUuid: localStorage.getItem('userId')
                }
            });

            if(response.status === 201){
                console.log(response.data.roomUuid);
                const roomId = response.data.roomUuid;
                navigate('/chatpage', 
                { 
                    state: { 
                        roomId : roomId, 
                        ownerName : "상대"
                    }
                });
            }
        }catch(error){
            console.log(error);
         }

    }
    return (
        <div className='MainPageClass'>
            <h2> 접속자 : { localStorage.getItem("username")} </h2>
            <h2> 유저 목록 </h2>
            <div>
                <table>
                    {/* 테이블 헤더 */}
                    <thead>
                        <tr>
                            <th>이름</th>
                            <th>Email</th>
                            <th>채팅하기</th>
                        </tr>
                    </thead>
                    {/* 테이블 바디 */}
                    <tbody>
                        {users.map((user, index) => (
                            <tr key={index}>
                                <td>{user.username}</td>
                                <td>{user.email}</td>
                                <td>
                                    <button onClick={(event)=>createChatRoom(event,user)}>채팅하기</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default MainPage;