package com.example.userservice.service;


import com.example.userservice.dto.ResponseUser;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ModelMapperUtil modelMapperUtil;

    @Override
    public UserDto createUser(UserDto userDto) {
        userRepository.save(modelMapperUtil.dtoToEntity(userDto));
        return userDto;
    }

    @Override
    public Page<ResponseUser> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(ResponseUser::new);
    }

    @Override
    public ResponseUser getUser(String userId) {
        return new ResponseUser(userRepository.findOneByUserId(userId));
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null) throw new UsernameNotFoundException(email);
        return modelMapperUtil.entityToDto(userEntity);
    }

    // Web Security 관련 메소드
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 DB에서 USER 정보 찾기
        UserEntity userEntity = userRepository.findByEmail(email);

        // 없으면 Exception 발생
        if(userEntity == null) throw new UsernameNotFoundException(email);

        // 있으면 UserDetails 객체 Return
        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true,true,true,true,
                 new ArrayList<>());
    }
}
