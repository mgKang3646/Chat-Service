package com.example.userservice.util;

import com.example.userservice.dto.RequestUser;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;


@RequiredArgsConstructor
@Getter
public class ModelMapperUtil {

    private final ModelMapper mapper;
    private final BCryptPasswordEncoder pwdEncoder;

    public UserEntity dtoToEntity(UserDto userDto){
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(pwdEncoder.encode(userDto.getPwd()));
        return userEntity;
    }

    public UserDto requestToDto(RequestUser requestUser){
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());
        return userDto;
    }

    public UserDto entityToDto(UserEntity userEntity){
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(userEntity, UserDto.class);
    }



}
