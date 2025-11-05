package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.service.UserService;
import com.subscription.subscription_system.utils.JwtUtils;
import com.subscription.subscription_system.validator.basicValidation.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserValidator userValidator;

    @PostMapping("/create/user")
    public ResponseEntity<UserCreateResponseDto> createUser(@RequestBody UserCreateRequestDto userCreateDto) throws CommonException {
        //Basic Validation for creating user
        log.info("Basic Validation for creating a user");
        userValidator.validateUserCreate(userCreateDto);
        //Creating user
        log.info("Creating a user");
        UserEntity user = userService.createUser(userCreateDto);

        UserCreateResponseDto response = new UserCreateResponseDto( "User created successfully", HttpStatus.CREATED.value(), user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/get/userdetails")
    public ResponseEntity<UserGetResponseDto> getUserDetails(@RequestBody UserGetRequestDto userGetRequestDto ) throws CommonException {

        //Creating user
        log.info("Getting a user details");
        UserEntity user = userService.getUserDetails(userGetRequestDto);

        UserCreateResponseDto response = new UserCreateResponseDto( "User created successfully", HttpStatus.CREATED.value(), user);
        return ResponseEntity.ok(response);
    }



}
