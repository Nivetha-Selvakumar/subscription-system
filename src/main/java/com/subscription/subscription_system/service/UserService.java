package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

@Component
public interface UserService{
    AuthTokenEntity signUpUser(SignupRequestDto userCreateDto) throws CommonException;

    PaginatedResponse<UserDetailsDto> getUsersList(String userId, String search, String filterBy, String sortBy, String sortDir, int offset,int limit) throws CommonException;

    UserDetailsDto editUser(String userId, String targetUserId, UserEditRequestDto editDto) throws CommonException;

    void deleteUser(String userId, String targetUserId) throws CommonException;

    UserEntity createUser(UserCreateRequestDto userCreateDto, String userId) throws CommonException;

    UserDetailsDto getUserDetails(String userId, String targetUserId) throws CommonException;
}
