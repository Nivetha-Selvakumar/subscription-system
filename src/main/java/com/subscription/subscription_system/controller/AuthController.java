package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.LoginRequestDto;
import com.subscription.subscription_system.dto.LoginResponseDto;
import com.subscription.subscription_system.dto.LogoutResponseDto;
import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.service.AuthService;
import com.subscription.subscription_system.validator.basicValidation.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/")
public class AuthController {

    @Autowired
    UserValidator userValidator;

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody LoginRequestDto loginRequestDto) throws CommonException {
        //Basic Validation for Login
        log.info("Basic Validation for Login user");
        userValidator.validateLoginUser(loginRequestDto);

        //Validating logging in
        log.info("Validating for Login user");
        AuthTokenEntity user = authService.loggingUser(loginRequestDto);

        LoginResponseDto response = new LoginResponseDto("Login Successfully", HttpStatus.OK.value(), user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDto> logoutUser(@RequestHeader("Authorization") String token) throws CommonException {
        log.info("Processing logout request...");

        authService.logoutUser(token);

        LogoutResponseDto response = new LogoutResponseDto(
                "Logout successful",
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }
}
