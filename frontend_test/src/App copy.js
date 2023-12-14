
// //https://surajsharma.net/blog/axios-post-form-data

// import React from 'react'
// import { useNavigate } from 'react-router-dom'
// import Chat from './Chat'
// import logo from './logo.svg';
// import './App.css';
// import './chat.css'
// import axios from 'axios';


// function App() {

//   const [joinValue,joinformValue]= React.useState({
//     email: '',
//     username: '',
//     pwd: ''
//   });

//   const [loginValue,loginformValue]= React.useState({
//     email: '',
//     pwd: ''
//   });


//   const changeJoinHandler = (event) => {
//     joinformValue({
//       ...joinValue,
//       [event.target.name]: event.target.value
//     });
//   }

//   const changeLoginHandler = (event) => {
//     loginformValue({
//       ...loginValue,
//       [event.target.name]: event.target.value
//     });
//   }



// const joinHandler = async(event) => {
//   const joinFormData = new FormData();
//   joinFormData.append("email",joinValue.email)
//   joinFormData.append("username",joinValue.username)
//   joinFormData.append("pwd",joinValue.pwd)

//   event.preventDefault();
  
//   try{
//     const response = await axios({
//       method: "post",
//       url: "/user-service/users",
//       data: joinFormData,
//       headers: { "Content-Type" : "application/json"}
//     });

//     console.log(response);

//   }catch(error){
//     console.log(error)
//   }
// }
  
// // eslint-disable-next-line no-unused-vars
// const loginHandler = async(event) => {
//     const loginFormData = new FormData();
//     loginFormData.append("email",loginValue.email);
//     loginFormData.append("pwd",loginValue.pwd);

//     event.preventDefault();
 
//     try{
//       const response = await axios({
//         method: "post",
//         url: "/user-service/login",
//         data: loginFormData,
//         headers: { "Content-Type" : "application/json"}
//       });

//       localStorage.setItem('userId',response.headers['userid']);
//       localStorage.setItem('jwt','Bearer ' + response.headers['token']);

//       console.log(localStorage.getItem('userId'));
//       console.log(localStorage.getItem('jwt'));
//       gotoMainPage();

//       // if(localStorage.getItem('userId') !== undefined ){
//       //   console.log("메인페이지이동!!!")
//       // }
//     }catch(error){
//       console.log(error)
//     }
//   }

//   // ===================== VIEW =====================
//   return (
//     <div className="App">
//       <header className="App-header">
//         <img src={logo} className="App-logo" alt="logo" />
//         <div className="container">
//           <h2>가입</h2>
//           {/* {lists&&lists.map((list,index)=>(
//             <li key={index}>{list.value}</li>
//           ))} */}
//           <form className="example" onSubmit={joinHandler}>
//             <input 
//               type="email"
//               name="email"
//               placeholder="email을 입력해주세요..."
//               onChange={changeJoinHandler} 
//               value={joinValue.email}     
//             />
//             <input 
//               type="text"
//               name="username"
//               placeholder="username를 입력해주세요..."
//               onChange={changeJoinHandler} 
//               value={joinValue.username}     
//             />
//             <input 
//               type="password"
//               name="pwd"
//               placeholder="Pwd를 입력해주세요..."
//               onChange={changeJoinHandler} 
//               value={joinValue.pwd}     
//             />
//             <button type="submit"> 가입 </button>
//           </form>
//           <h2>로그인</h2>
//           <form className="example" onSubmit={loginHandler}>
//             <input 
//               type="email"
//               name="email"
//               placeholder="email을 입력해주세요..."
//               onChange={changeLoginHandler} 
//               value={loginValue.email}     
//             />
//             <input 
//               type="password"
//               name="pwd"
//               placeholder="Pwd를 입력해주세요..."
//               onChange={changeLoginHandler} 
//               value={loginValue.pwd}     
//             />
//             <button type="submit"> 로그인 </button>
//           </form>

//           <Chat />


//         </div>


//       </header>
//     </div>
//   );
// }

// export default App;
