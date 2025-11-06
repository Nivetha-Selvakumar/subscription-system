package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.UserCreateRequestDto;
import com.subscription.subscription_system.dto.UserDetailsDto;
import com.subscription.subscription_system.dto.UserDetailsRequestDto;
import com.subscription.subscription_system.dto.UserEditRequestDto;
import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserService{
    AuthTokenEntity createUser(UserCreateRequestDto userCreateDto) throws CommonException;

    UserDetailsDto getUserDetails(UserDetailsRequestDto userDetails) throws CommonException;

    List<UserDetailsDto> getUsersList(String userId, String search, String filterBy, String sortBy, String sortDir) throws CommonException;

    UserDetailsDto editUser(String userId, String targetUserId, UserEditRequestDto editDto) throws CommonException;

    void deleteUser(String userId, String targetUserId) throws CommonException;
}
