package com.subscription.subscription_system.mapper;

import com.subscription.subscription_system.dto.UserCreateDto;
import com.subscription.subscription_system.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity mapUserDtoToUserEntity(UserCreateDto userCreateDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(userCreateDto.getFirstName());
        userEntity.setLastName(userCreateDto.getLastName());
        userEntity.setEmail(userCreateDto.getEmail());
        userEntity.setPassword(userCreateDto.getPassword());
        userEntity.setAddress(userCreateDto.getAddress());
        userEntity.setDob(userCreateDto.getDateOfBirth());
        userEntity.setPhoneNumber(userCreateDto.getPhoneNumber());
        userEntity.setSex(userCreateDto.getSex());
        userEntity.setRole(userCreateDto.getRole());
        return userEntity;
    }

}
