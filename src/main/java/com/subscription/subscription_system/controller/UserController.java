package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.UserCreateDto;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserEntity> createUser(@RequestBody UserCreateDto userCreateDto){
        UserEntity createdUser = userService.createUser(userCreateDto);
        return ResponseEntity.ok(createdUser);
    }





}
