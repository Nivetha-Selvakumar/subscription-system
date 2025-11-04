package com.subscription.subscription_system.validation;

import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.repository.AdminRepo;
import com.subscription.subscription_system.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

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
}
