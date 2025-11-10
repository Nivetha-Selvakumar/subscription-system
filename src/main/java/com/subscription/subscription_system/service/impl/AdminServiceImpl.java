package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.AdminCreateRequestDto;
import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumUserType;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.AdminMapper;
import com.subscription.subscription_system.mapper.UserMapper;
import com.subscription.subscription_system.repository.AdminRepo;
import com.subscription.subscription_system.repository.UserRepo;
import com.subscription.subscription_system.service.AdminService;
import com.subscription.subscription_system.validation.BusinessValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminServiceImpl implements AdminService {

    @Autowired
    BusinessValidation businessValidation;

    @Autowired
    AdminRepo adminRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AdminMapper adminMapper;


    @Override
    public AdminEntity createAdmin(AdminCreateRequestDto adminCreateDto) throws CommonException {
        // Step 1: Check if user already exists by email
        UserEntity existingUser = businessValidation.getUserByEmailAdmin(adminCreateDto.getEmail());

        // Step 2: If user exists
        if (existingUser != null) {
            // Check if already an admin
            businessValidation.getAdminByUserEntity(existingUser);

            existingUser.setRole(EnumUserType.ADMIN);
            userRepo.save(existingUser);
            // Create new Admin
            AdminEntity adminEntity = adminMapper.mapAdminDtoToAdminEntity(adminCreateDto,existingUser);
            return adminRepo.save(adminEntity);
        }

        // Step 3: If user doesn't exist, create new user first
        // Save User
        UserEntity newUser = userMapper.mapUserDtoToUserEntityAdmin(adminCreateDto);
        userRepo.save(newUser);

        // Step 4: Create Admin entry linked to new user
        AdminEntity adminEntity = adminMapper.mapAdminDtoToAdminEntity(adminCreateDto,newUser);
        return adminRepo.save(adminEntity);
    }

}
