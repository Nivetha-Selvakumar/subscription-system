package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.LoginRequestDto;
import com.subscription.subscription_system.dto.UserCreateRequestDto;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.UserMapper;
import com.subscription.subscription_system.repository.AdminRepo;
import com.subscription.subscription_system.repository.SubscriberRepo;
import com.subscription.subscription_system.repository.UserRepo;
import com.subscription.subscription_system.service.UserService;
import com.subscription.subscription_system.validation.BusinessValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserMapper userMapper;

    @Autowired
    BusinessValidation businessValidation;

    @Autowired
    AdminRepo adminRepo;

    @Autowired
    SubscriberRepo subscriberRepo;

    @Override
    public UserEntity createUser(UserCreateRequestDto userCreateDto) throws CommonException {

        //check duplicate email
        businessValidation.getUserByEmail(userCreateDto.getEmail());

        // Save User
        UserEntity userEntity = userMapper.mapUserDtoToUserEntity(userCreateDto);
        userRepo.save(userEntity);
//       if (Objects.equals(userCreateDto.getRole(), EnumUserType.SUBSCRIBER.getValue())) {
//            SubscriberEntity subscriberEntity = new SubscriberEntity();
//            subscriberEntity.setUser(userEntity);
//            subscriberEntity.setCurrentSubStatus("Inactive");
//            subscriberEntity.setJoinDate(LocalDateTime.now().toString());
//            subscriberRepo.save(subscriberEntity);
//        }
        //Return user values
        return (userEntity);
    }

    @Override
    public UserEntity loggingUser(LoginRequestDto loginRequestDto) throws CommonException {
        UserEntity user = userRepo.findByEmail(loginRequestDto.getEmail());
        if(user == null ){
            throw new CommonException("User does not exist", HttpStatus.CONFLICT.value());
        }

        if(!user.getPassword().equals(loginRequestDto.getPassword())){
            throw new CommonException("Password doesn't match", HttpStatus.BAD_REQUEST.value());
        }

        return user;
    }
}
