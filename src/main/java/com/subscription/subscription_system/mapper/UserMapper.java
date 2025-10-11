package com.subscription.subscription_system.mapper;

import com.subscription.subscription_system.dto.UserCreateDto;
import com.subscription.subscription_system.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity mapUserDtoToUserEntity(UserCreateDto userCreateDto){
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userCreateDto.getName());
        userEntity.setEmpId(userCreateDto.getEmpId());
        userEntity.setSex(userCreateDto.getSex());
        userEntity.setEmail(userCreateDto.getEmail());
        userEntity.setPhoneNumber(userCreateDto.getPhoneNumber());
        userEntity.setAddress(userCreateDto.getAddress());

        return userEntity;

    }

}
