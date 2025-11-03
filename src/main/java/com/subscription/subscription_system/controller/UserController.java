package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.UserCreateDto;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.service.UserService;
import com.subscription.subscription_system.validator.basicValidation.UserValidator;
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

    @Autowired
    UserValidator userValidator;

    @PostMapping("/create/user")
    public ResponseEntity<String> createUser(@RequestBody UserCreateDto userCreateDto) throws CommonException {
        //Basic Validation for creating user
        log.info("Basic Validation for creating a user");
        userValidator.validateUserCreate(userCreateDto);
        //Creating user
        log.info("Creating a user");
        String user = userService.createUser(userCreateDto);
        return ResponseEntity.ok(user);
    }





}
