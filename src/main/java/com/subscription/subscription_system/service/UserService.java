package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.UserCreateDto;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

@Component
public interface UserService{
    String createUser(UserCreateDto userCreateDto) throws CommonException;

}
