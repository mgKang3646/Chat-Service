
import { Link } from 'react-router-dom';
import React from 'react';
import './Nav.css'

function Nav(){

    return (
        <div className='Nav'>
            <Link className="navBar" to={'/'}> 메인화면 </Link>
            <Link className="navBar" to={'/login'}> 로그인 </Link>
            <Link className="navBar" to={'/join'}> 회원가입 </Link>
            <Link className="navBar" to={'/chatlist'}> 채팅리스트 </Link>
        </div>
    );
}

export default Nav;
