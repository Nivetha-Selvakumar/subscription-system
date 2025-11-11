package com.subscription.subscription_system.mapper;

import com.subscription.subscription_system.dto.AdminCreateRequestDto;
import com.subscription.subscription_system.dto.SignupRequestDto;
import com.subscription.subscription_system.dto.UserCreateRequestDto;
import com.subscription.subscription_system.dto.UserDetailsDto;
import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.SubscriberEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumSexType;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumUserType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public UserEntity mapSignupDtoToUserEntity(SignupRequestDto userCreateDto, String user) {
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
        userEntity.setStatus(EnumStatusType.ACTIVE);
        // ✅ For signup flow (self-created)
        if (user == null) {
            userEntity.setCreatedBy("SELF");
            userEntity.setUpdatedBy("SELF");
        } else {
            userEntity.setCreatedBy(user);
            userEntity.setUpdatedBy(user);
        }
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());

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
        userEntity.setStatus(EnumStatusType.ACTIVE);
        return userEntity;
    }

    public UserDetailsDto mapUserDetails(UserEntity user, AdminEntity adminEntity, SubscriberEntity subscriberEntity) {
        UserDetailsDto userDetailsDto = new UserDetailsDto();

        userDetailsDto.setId(user.getId());
        userDetailsDto.setFirstName(user.getFirstName());
        userDetailsDto.setLastName(user.getLastName());
        userDetailsDto.setEmail(user.getEmail());
        userDetailsDto.setPassword(user.getPassword());
        userDetailsDto.setDob(user.getDob());
        userDetailsDto.setPhoneNumber(user.getPhoneNumber());
        userDetailsDto.setSex(user.getSex().getName());
        userDetailsDto.setRole(user.getRole().getValue());
        userDetailsDto.setAddress(user.getAddress());
        userDetailsDto.setStatus(user.getStatus().getName());

        if (adminEntity != null) {
            userDetailsDto.setSalary(adminEntity.getSalary());
        } else {
            userDetailsDto.setSalary(null);
        }

        if (subscriberEntity != null) {
            userDetailsDto.setCurrentSubStatus(subscriberEntity.getCurrentSubStatus());
            userDetailsDto.setSubStartDate(subscriberEntity.getSubStartDate());
            userDetailsDto.setSubEndDate(subscriberEntity.getSubEndDate());
            userDetailsDto.setJoinDate(subscriberEntity.getJoinDate());
        } else {
            userDetailsDto.setCurrentSubStatus(null);
            userDetailsDto.setSubStartDate(null);
            userDetailsDto.setSubEndDate(null);
            userDetailsDto.setJoinDate(null);
        }

        return userDetailsDto;
    }

    public UserEntity mapUserDtoToUserEntity(UserCreateRequestDto userCreateDto, String user) {
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
        userEntity.setStatus(EnumStatusType.ACTIVE);
        // ✅ For signup flow (self-created)
        if (user == null) {
            userEntity.setCreatedBy("SELF");
            userEntity.setUpdatedBy("SELF");
        } else {
            userEntity.setCreatedBy(user);
            userEntity.setUpdatedBy(user);
        }
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());

        return userEntity;
    }
}
