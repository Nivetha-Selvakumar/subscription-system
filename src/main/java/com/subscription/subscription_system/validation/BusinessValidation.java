package com.subscription.subscription_system.validation;

import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumUserType;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.repository.AdminRepo;
import com.subscription.subscription_system.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class BusinessValidation {

    @Autowired
    UserRepo userRepo;

    @Autowired
    AdminRepo adminRepo;

    public BusinessValidation(UserRepo userRepo, AdminRepo adminRepo) {
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
    }


    public void getUserByEmail(String email) throws CommonException {
        UserEntity existingUser = userRepo.findByEmail(email);
        if (existingUser != null) {
            throw new CommonException("User with Email '" + email + "' already exists", HttpStatus.CONFLICT.value());
        }
    }
    public UserEntity getUserByEmailAdmin(String email){
        return  userRepo.findByEmail(email);
    }

    public void getAdminByUserEntity(UserEntity user) {
        AdminEntity existingAdmin = adminRepo.findByUser(user);
        if (existingAdmin != null) {
            throw new RuntimeException("Admin already exists for this user");
        }
    }

    public void validateUserList(String userId) throws CommonException {
        Optional<UserEntity> userOpt = userRepo.findByIdAndStatus(userId, EnumStatusType.ACTIVE);
        if (userOpt.isEmpty()) {
            throw new CommonException("Invalid user ID — user not found",HttpStatus.BAD_REQUEST.value());
        }
        UserEntity requestingUser = userOpt.get();
        if (!EnumUserType.ADMIN.getValue().equalsIgnoreCase(requestingUser.getRole().getValue())) {
            throw new CommonException("Access denied — only admins can view user list",HttpStatus.BAD_REQUEST.value());
        }

        Optional<AdminEntity> existingAdmin = adminRepo.findByUserAndStatus(requestingUser,EnumStatusType.ACTIVE);
        if (existingAdmin.isEmpty()) {
            throw new CommonException("Admin record not found or inactive for this user",
                    HttpStatus.BAD_REQUEST.value());
        }
    }
}
