package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.LoginRequestDto;
import com.subscription.subscription_system.dto.UserCreateRequestDto;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

@Component
public interface UserService{
    UserEntity createUser(UserCreateRequestDto userCreateDto) throws CommonException;

    UserEntity loggingUser(LoginRequestDto loginRequestDto) throws CommonException;
}
