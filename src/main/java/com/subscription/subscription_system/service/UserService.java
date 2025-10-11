package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.UserCreateDto;
import com.subscription.subscription_system.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public interface UserService{
    UserEntity createUser(UserCreateDto userCreateDto);

}
