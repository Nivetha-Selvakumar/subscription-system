package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.service.UserService;
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

    @PostMapping("/signup/user")
    public ResponseEntity<UserCreateResponseDto> signUpUser(@RequestBody SignupRequestDto userCreateDto) throws CommonException {
        //Basic Validation for creating user
        log.info("Basic Validation for signing up user");
        userValidator.validateUserSignup(userCreateDto);
        //Creating user
        log.info("Creating a new user self");
        AuthTokenEntity user = userService.signUpUser(userCreateDto);

        UserCreateResponseDto response = new UserCreateResponseDto("User created successfully", HttpStatus.CREATED.value(), user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/userdetails")
    public ResponseEntity<UserDetailsResponseDto> getUserDetails(
            @RequestHeader("User-Id") String userId,
            @RequestParam("targetUserId") String targetUserId
    ) throws CommonException {

        log.info("Basic Validation for getting user details");
        userValidator.validateUserDetails(userId,targetUserId);

        //Creating user
        log.info("Getting a user details");
        UserDetailsDto user = userService.getUserDetails(userId,targetUserId);

        UserDetailsResponseDto response = new UserDetailsResponseDto("Got User details successfully", HttpStatus.OK.value(), user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/users")
    public ResponseEntity<UserGetResponseDto> getUsers(@RequestHeader("User-Id") String userId,
                                                       @RequestParam(required = false) String search,
                                                       @RequestParam(required = false) String filterBy,  // format: key:value,key:value
                                                       @RequestParam(required = false, defaultValue = "firstName") String sortBy,
                                                       @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                       @RequestParam(required = false, defaultValue = "0") int offset,
                                                       @RequestParam(required = false, defaultValue = "10") int limit) throws CommonException {
        log.info("Basic validation for getting user List");
        userValidator.validateUserList(userId);

        // Fetch user details
        log.info("Fetching user details list");
        PaginatedResponse<UserDetailsDto> users = userService.getUsersList(userId, search, filterBy, sortBy, sortDir, offset, limit);

        UserGetResponseDto response = new UserGetResponseDto("Got User Data successfully", HttpStatus.OK.value(), users);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit/user")
    public ResponseEntity<UserEditResponseDto> editUser(
            @RequestHeader("User-Id") String userId,
            @RequestParam("targetUserId") String targetUserId,
            @RequestBody UserEditRequestDto editDto) throws CommonException {

        log.info("Validating edit permission for user: {}", userId);
        if (userId == null || targetUserId == null) {
            throw new CommonException("UserId and Targeted User Id are Mandatory", HttpStatus.BAD_REQUEST.value());
        }
        userValidator.validateEditUser(userId, targetUserId, editDto);

        log.info("Editing user details for ID: {}", targetUserId);
        UserDetailsDto updatedUser = userService.editUser(userId, targetUserId, editDto);

        UserEditResponseDto response = new UserEditResponseDto(
                "User updated successfully", HttpStatus.OK.value(), updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/user")
    public ResponseEntity<CommonResponseDto> deleteUser(
            @RequestHeader String userId,
            @RequestParam("targetUserId") String targetUserId) throws CommonException {

        log.info("Validating delete permission for user: {}", userId);
        userValidator.validateDeleteUser(userId, targetUserId);

        log.info("Deleting user with ID: {}", targetUserId);
        userService.deleteUser(userId, targetUserId);

        CommonResponseDto response = new CommonResponseDto(
                "User deleted successfully", HttpStatus.OK.value(), null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create/user")
    public ResponseEntity<UserCreateResponseDto> createUser(@RequestBody UserCreateRequestDto userCreateDto, @RequestParam("User-Id") String userId) throws CommonException {
        //Basic Validation for creating user
        log.info("Basic Validation for creating a user");
        userValidator.validateUserCreate(userCreateDto, userId);
        //Creating user
        log.info("Creating a user");
        UserEntity user = userService.createUser(userCreateDto, userId);

        UserCreateResponseDto response = new UserCreateResponseDto("User created successfully", HttpStatus.CREATED.value(), user);
        return ResponseEntity.ok(response);
    }
}
