import React from 'react'
import { useNavigate } from 'react-router-dom'
import './LoginPage.css';
import axios from 'axios';


function LoginPage() {

    let navigate = useNavigate();

    const [loginValue,loginformValue]= React.useState({
        email: '',
        pwd: ''
      });
    
    const changeLoginHandler = (event) => {
        loginformValue({
          ...loginValue,
          [event.target.name]: event.target.value
        });
    }

    const loginHandler = async(event) => {
        const loginFormData = new FormData();
        loginFormData.append("email",loginValue.email);
        loginFormData.append("pwd",loginValue.pwd);
    
        event.preventDefault();
     
        try{
          const response = await axios({
            method: "post",
            url: "/user-service/login",
            data: loginFormData,
            headers: { "Content-Type" : "application/json" }
          });
    
          // localStorage.setItem('userId',response.headers['userid']);
          // localStorage.setItem('jwt','Bearer ' + response.headers['token']);
    
          // console.log(localStorage.getItem('userId'));
          // console.log(localStorage.getItem('jwt'));

          if(response.status === 200){
            localStorage.setItem('userId',response.data.userId);
            localStorage.setItem('jwt','Bearer ' + response.data.jwt);
            localStorage.setItem('username',response.data.username);

                
            console.log(localStorage.getItem('userId'));
            console.log(localStorage.getItem('jwt'));
            console.log(localStorage.getItem('username'));
            navigate('/'); // 메인 페이지로 이동
          }
    
          // if(localStorage.getItem('userId') !== undefined ){
          //   console.log("메인페이지이동!!!")
          // }
        }catch(error){
          console.log(error)
        }
      }

    return (
         <div className='LoginClass'>
            <form className="example" onSubmit={loginHandler}>
                <h2>로그인</h2>
                <input 
                type="email"
                name="email"
                placeholder="email을 입력해주세요..."
                onChange={changeLoginHandler} 
                value={loginValue.email}     
                />
                <input 
                type="password"
                name="pwd"
                placeholder="Pwd를 입력해주세요..."
                onChange={changeLoginHandler} 
                value={loginValue.pwd}     
                />
                <button type="submit"> 로그인 </button>
            </form>
        </div>
    );
}

export default LoginPage;