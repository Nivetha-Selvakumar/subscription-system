package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.LoginRequestDto;
import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

@Component
public interface AuthService {
    AuthTokenEntity loggingUser(LoginRequestDto loginRequestDto) throws CommonException;

    void logoutUser(String token) throws CommonException;
}
