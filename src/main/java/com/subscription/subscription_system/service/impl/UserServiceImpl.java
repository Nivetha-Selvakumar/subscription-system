package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.UserCreateDto;
import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.SubscriberEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumUserType;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.UserMapper;
import com.subscription.subscription_system.repository.AdminRepo;
import com.subscription.subscription_system.repository.SubscriberRepo;
import com.subscription.subscription_system.repository.UserRepo;
import com.subscription.subscription_system.service.UserService;
import com.subscription.subscription_system.validation.BusinessValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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
    public String createUser(UserCreateDto userCreateDto) throws CommonException {

        //check duplicate email or empId
        businessValidation.getUserByEmailOrEmpId(userCreateDto.getEmail(), userCreateDto.getEmpId());

        // Save User
        UserEntity userEntity = userMapper.mapUserDtoToUserEntity(userCreateDto);

        //If Admin
        if(Objects.equals(userCreateDto.getRole(), EnumUserType.ADMIN.getValue())){
            AdminEntity adminEntity = new AdminEntity();
            adminEntity.setUser(userEntity);
            adminEntity.setSalary(0.0);
            adminRepo.save(adminEntity);
        }else if (Objects.equals(userCreateDto.getRole(), EnumUserType.SUBSCRIBER.getValue())) {
            SubscriberEntity subscriberEntity = new SubscriberEntity();
            subscriberEntity.setUser(userEntity);
            subscriberEntity.setCurrentSubStatus("Inactive");
            subscriberEntity.setJoinDate(LocalDateTime.now().toString());
            subscriberRepo.save(subscriberEntity);
        }

        //Return user values
        return("User created Successfully") ;
    }
}
