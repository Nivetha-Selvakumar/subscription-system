package com.subscription.subscription_system.validation;

import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.SubscriptionPlanEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumPlanType;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumUserType;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.repository.AdminRepo;
import com.subscription.subscription_system.repository.SubscriptionPlanRepo;
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

    @Autowired
    SubscriptionPlanRepo subscriptionPlanRepo;

    public BusinessValidation(UserRepo userRepo, AdminRepo adminRepo) {
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
    }


    public void getUserByEmail(String email) throws CommonException {
        UserEntity existingUser = userRepo.findByEmailAndStatus(email,EnumStatusType.ACTIVE);
        if (existingUser != null) {
            throw new CommonException("User with Email '" + email + "' already exists", HttpStatus.CONFLICT.value());
        }
    }
    public UserEntity getUserByEmailAdmin(String email){
        return  userRepo.findByEmail(email);
    }

    public void getAdminByUserEntity(UserEntity user) throws CommonException {
        Optional<AdminEntity> existingAdmin = adminRepo.findByUserAndStatus(user,EnumStatusType.ACTIVE);
        if (existingAdmin.isPresent()) {
            throw new CommonException("Admin already exists for this user",HttpStatus.BAD_REQUEST.value());
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

    public UserEntity validateSelfOrAdmin(String requesterId, String targetUserId) throws CommonException {
        Optional<UserEntity> requesterOpt = userRepo.findByIdAndStatus(requesterId, EnumStatusType.ACTIVE);
        if (requesterOpt.isEmpty()) {
            throw new CommonException("Invalid requester ID — user not found", HttpStatus.BAD_REQUEST.value());
        }

        UserEntity requester = requesterOpt.get();

        // ✅ Allow if admin
        if (EnumUserType.ADMIN.getValue().equalsIgnoreCase(requester.getRole().getValue())) {
            return requester;
        }

        // ✅ Allow if editing/deleting own record
        if (requester.getId().equals(targetUserId)) {
            return requester;
        }

        throw new CommonException("Access denied — you cannot modify other users",
                HttpStatus.FORBIDDEN.value());
    }


    public UserEntity getAdminByUserId(String userId) throws CommonException {
        Optional<UserEntity> existingAdmin = userRepo.findByIdAndStatus(userId,EnumStatusType.ACTIVE);
        if (existingAdmin.isEmpty()) {
            throw new CommonException("Admin User is not found",HttpStatus.BAD_REQUEST.value());
        }
        return existingAdmin.get();
    }

    public void checkAdminOrNot(UserEntity user) throws CommonException {
        Optional<AdminEntity> existingAdmin = adminRepo.findByUserAndStatus(user,EnumStatusType.ACTIVE);
        if (existingAdmin.isEmpty()) {
            throw new CommonException("Not a Admin",HttpStatus.BAD_REQUEST.value());
        }
    }

    public void validateSubscriptionNameExist(String planName, String planType) throws CommonException {
        SubscriptionPlanEntity subscriptionPlan =
                subscriptionPlanRepo.findByPlanNameAndPlanTypeAndStatus(
                        planName,
                        EnumPlanType.fromValue(planType.toUpperCase()),
                        EnumStatusType.ACTIVE
                );
        if(subscriptionPlan != null){
            throw new CommonException("Subscription Plan name already exist for this plan type",HttpStatus.BAD_REQUEST.value());
        }
    }

    public void validateUserOrNot(String userId) throws CommonException {
        Optional<UserEntity> user = userRepo.findByIdAndStatus(userId,EnumStatusType.ACTIVE);
        if(user.isEmpty()){
            throw new CommonException("User Not Exist",HttpStatus.BAD_REQUEST.value());
        }
    }

    public SubscriptionPlanEntity subscriptionPlanExist(String planId) throws CommonException {
        Optional<SubscriptionPlanEntity> subscriptionPlanEntity = subscriptionPlanRepo.findByIdAndStatus(planId, EnumStatusType.ACTIVE);
        if(subscriptionPlanEntity.isEmpty()){
            throw new CommonException("Plan Not Exist",HttpStatus.BAD_REQUEST.value());
        }
        return subscriptionPlanEntity.get();
    }
}
