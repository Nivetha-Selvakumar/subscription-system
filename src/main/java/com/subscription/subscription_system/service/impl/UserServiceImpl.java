package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.UserCreateDto;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.mapper.UserMapper;
import com.subscription.subscription_system.repository.UserRepo;
import com.subscription.subscription_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserMapper userMapper;

    @Override
    public UserEntity createUser(UserCreateDto userCreateDto) {
        return userRepo.save(userMapper.mapUserDtoToUserEntity(userCreateDto));
    }
}
