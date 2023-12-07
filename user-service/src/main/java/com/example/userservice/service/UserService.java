package com.example.userservice.service;

import com.example.userservice.dto.ResponseUser;
import com.example.userservice.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    public UserDto createUser(UserDto userDto);

    public Page<ResponseUser> getAllUsers(Pageable pageable);

    public ResponseUser getUser(String userId);

    public UserDto getUserDetailsByEmail(String email);

}
