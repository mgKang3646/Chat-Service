
//https://surajsharma.net/blog/axios-post-form-data

import React from 'react'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import LoginPage from './LoginPage'
import MainPage from './MainPage'
import JoinPage from './JoinPage'
import ChatPage from './ChatPage'
import ChatListPage from './ChatListPage'
import Header from './component/Header'
import Nav from './component/Nav'


function App() {
  return (
    <div className='App'>
      <Header />
      <Router>
        <Nav />
        <Routes>
            <Route path="/login" element={<LoginPage/>}/>
            <Route path="/join" element={<JoinPage/>}/>
            <Route path="/" element={<MainPage/>}/>
            <Route path="/chatpage" element={<ChatPage/>}/>
            <Route path="/chatlist" element={<ChatListPage/>}/>
        </Routes>
      </Router>
    </div>

  );
}

export default App;
