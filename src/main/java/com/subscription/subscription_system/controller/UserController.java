package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.service.UserService;
import com.subscription.subscription_system.validator.basicValidation.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        AuthTokenEntity user = userService.createUser(userCreateDto);

        UserCreateResponseDto response = new UserCreateResponseDto( "User created successfully", HttpStatus.CREATED.value(), user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/userdetails")
    public ResponseEntity<UserDetailsResponseDto> getUserDetails(@RequestBody UserDetailsRequestDto userDetails ) throws CommonException {

        log.info("Basic Validation for getting user details");
        userValidator.validateUserDetails(userDetails);

        //Creating user
        log.info("Getting a user details");
        UserDetailsDto user = userService.getUserDetails(userDetails);

        UserDetailsResponseDto response = new UserDetailsResponseDto( "Got User details successfully", HttpStatus.OK.value(), user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/users")
    public ResponseEntity<UserGetResponseDto> getUsers( @RequestHeader("User-Id") String userId,
                                                        @RequestParam(required = false) String search,
                                                        @RequestParam(required = false) String filterBy,  // format: key:value,key:value
                                                        @RequestParam(required = false, defaultValue = "firstName") String sortBy,
                                                        @RequestParam(required = false, defaultValue = "asc") String sortDir) throws CommonException {
        log.info("Basic validation for getting user details");
        userValidator.validateUserList(userId);

        // Fetch user details
        log.info("Fetching user details list");
        List<UserDetailsDto> users = userService.getUsersList(userId, search, filterBy, sortBy, sortDir);

        UserGetResponseDto response = new UserGetResponseDto( "Got User Data successfully", HttpStatus.OK.value(), users);
        return ResponseEntity.ok(response);
    }


}
