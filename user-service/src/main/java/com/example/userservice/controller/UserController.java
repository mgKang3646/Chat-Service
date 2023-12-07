package com.example.userservice.controller;

import com.example.userservice.dto.RequestUser;
import com.example.userservice.dto.ResponseUser;
import com.example.userservice.service.UserService;
import com.example.userservice.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapperUtil modelMapperUtil;

    @GetMapping("/health_check")
    public String status(){
        return "It's Working In User Service";
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public String createUser(@RequestBody RequestUser user){
        userService.createUser(modelMapperUtil.requestToDto(user));
        return "Creating User is completed!";
    }


    @GetMapping("/users")
    public Page<ResponseUser> getUsers(@PageableDefault(size = 5) Pageable pageable){
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/users/{userId}")
    public ResponseUser getUser(@PathVariable String userId){
        return userService.getUser(userId);
    }





}
