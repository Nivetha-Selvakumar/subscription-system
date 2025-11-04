package com.subscription.subscription_system.mapper;

import com.subscription.subscription_system.dto.AdminCreateRequestDto;
import com.subscription.subscription_system.dto.UserCreateRequestDto;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumSexType;
import com.subscription.subscription_system.enumuration.EnumUserType;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity mapUserDtoToUserEntity(UserCreateRequestDto userCreateDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(userCreateDto.getFirstName());
        userEntity.setLastName(userCreateDto.getLastName());
        userEntity.setEmail(userCreateDto.getEmail());
        userEntity.setPassword(userCreateDto.getPassword());
        userEntity.setAddress(userCreateDto.getAddress());
        userEntity.setDob(userCreateDto.getDateOfBirth());
        userEntity.setPhoneNumber(userCreateDto.getPhoneNumber());
        userEntity.setSex(EnumSexType.valueOf(userCreateDto.getSex()));
        userEntity.setRole(
                userCreateDto.getRole() != null
                        ? EnumUserType.valueOf(userCreateDto.getRole().toUpperCase())
                        : EnumUserType.USER
        );
        return userEntity;
    }

    public UserEntity mapUserDtoToUserEntityAdmin(AdminCreateRequestDto adminCreateDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(adminCreateDto.getFirstName());
        userEntity.setLastName(adminCreateDto.getLastName());
        userEntity.setEmail(adminCreateDto.getEmail());
        userEntity.setPassword(adminCreateDto.getPassword());
        userEntity.setAddress(adminCreateDto.getAddress());
        userEntity.setDob(adminCreateDto.getDateOfBirth());
        userEntity.setPhoneNumber(adminCreateDto.getPhoneNumber());
        userEntity.setSex(EnumSexType.valueOf(adminCreateDto.getSex()));
        userEntity.setRole(
                adminCreateDto.getRole() != null
                        ? EnumUserType.valueOf(adminCreateDto.getRole().toUpperCase())
                        : EnumUserType.USER
        );
        return userEntity;
    }

}
