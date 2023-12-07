import React from 'react'
import './JoinPage.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'



function JoinPage(){

   let navigate = useNavigate();

    const [joinValue,joinformValue]= React.useState({
        email: '',
        username: '',
        pwd: ''
    });

    const changeJoinHandler = (event) => {
        joinformValue({
          ...joinValue,
          [event.target.name]: event.target.value
        });
    }

    const joinHandler = async(event) => {
        const joinFormData = new FormData();
        joinFormData.append("email",joinValue.email)
        joinFormData.append("username",joinValue.username)
        joinFormData.append("pwd",joinValue.pwd)
      
        event.preventDefault();
        
        try{
          const response = await axios({
            method: "post",
            url: "/user-service/users",
            data: joinFormData,
            headers: { "Content-Type" : "application/json"}
          });
      
          if(response.status === 201){
            console.log(response);
            navigate('/'); // 메인 페이지로 이동
          }
      
        }catch(error){
          console.log(error)
        }
      }

    return (
        <div className='JoinPageClass'>
          <form className="example" onSubmit={joinHandler}>
            <h2>가입</h2>

            <input 
              type="email"
              name="email"
              placeholder="email을 입력해주세요..."
              onChange={changeJoinHandler} 
              value={joinValue.email}     
            />
            <input 
              type="text"
              name="username"
              placeholder="username를 입력해주세요..."
              onChange={changeJoinHandler} 
              value={joinValue.username}     
            />
            <input 
              type="password"
              name="pwd"
              placeholder="Pwd를 입력해주세요..."
              onChange={changeJoinHandler} 
              value={joinValue.pwd}     
            />
            <button type="submit"> 가입 </button>
          </form>
        </div>

    );
}

export default JoinPage;