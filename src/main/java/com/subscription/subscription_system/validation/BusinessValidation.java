package com.subscription.subscription_system.validation;

import com.subscription.subscription_system.entity.*;
import com.subscription.subscription_system.enumuration.*;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.util.List;
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

    @Autowired
    FeedbackRepo feedbackRepo;

    @Autowired
    SubscriberRepo subscriberRepo;

    @Autowired
    PaymentRepo paymentRepo;

    @Autowired
    MongoTemplate mongoTemplate;

    public BusinessValidation(UserRepo userRepo, AdminRepo adminRepo) {
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
    }


    public void getUserByEmail(String email) throws CommonException {
        UserEntity existingUser = userRepo.findByEmailAndStatus(email, EnumStatusType.ACTIVE);
        if (existingUser != null) {
            throw new CommonException("User with Email '" + email + "' already exists", HttpStatus.CONFLICT.value());
        }
    }

    public UserEntity getUserByEmailAdmin(String email) {
        return userRepo.findByEmail(email);
    }

    public void getAdminByUserEntity(UserEntity user) throws CommonException {
        Optional<AdminEntity> existingAdmin = adminRepo.findByUserAndStatus(user, EnumStatusType.ACTIVE);
        if (existingAdmin.isPresent()) {
            throw new CommonException("Admin already exists for this user", HttpStatus.BAD_REQUEST.value());
        }
    }

    public void validateUserList(String userId) throws CommonException {
        Optional<UserEntity> userOpt = userRepo.findByIdAndStatus(userId, EnumStatusType.ACTIVE);
        if (userOpt.isEmpty()) {
            throw new CommonException("Invalid user ID — user not found", HttpStatus.BAD_REQUEST.value());
        }
        UserEntity requestingUser = userOpt.get();
        if (!EnumUserType.ADMIN.getValue().equalsIgnoreCase(requestingUser.getRole().getValue())) {
            throw new CommonException("Access denied — only admins can view user list", HttpStatus.BAD_REQUEST.value());
        }

        Optional<AdminEntity> existingAdmin = adminRepo.findByUserAndStatus(requestingUser, EnumStatusType.ACTIVE);
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
        Optional<UserEntity> existingAdmin = userRepo.findByIdAndStatus(userId, EnumStatusType.ACTIVE);
        if (existingAdmin.isEmpty()) {
            throw new CommonException("Admin User is not found", HttpStatus.BAD_REQUEST.value());
        }
        return existingAdmin.get();
    }

    public void checkAdminOrNot(UserEntity user) throws CommonException {
        Optional<AdminEntity> existingAdmin = adminRepo.findByUserAndStatus(user, EnumStatusType.ACTIVE);
        if (existingAdmin.isEmpty()) {
            throw new CommonException("Not a Admin", HttpStatus.BAD_REQUEST.value());
        }
    }

    public void validateSubscriptionNameExist(String planName, String planType) throws CommonException {
        SubscriptionPlanEntity subscriptionPlan =
                subscriptionPlanRepo.findByPlanNameAndPlanTypeAndStatus(
                        planName,
                        EnumPlanType.fromValue(planType.toUpperCase()),
                        EnumStatusType.ACTIVE
                );
        if (subscriptionPlan != null) {
            throw new CommonException("Subscription Plan name already exist for this plan type", HttpStatus.BAD_REQUEST.value());
        }
    }

    public UserEntity validateUserOrNot(String userId) throws CommonException {
        Optional<UserEntity> user = userRepo.findByIdAndStatus(userId, EnumStatusType.ACTIVE);
        if (user.isEmpty()) {
            throw new CommonException("User Not Exist", HttpStatus.BAD_REQUEST.value());
        }
        return user.get();
    }

    public SubscriptionPlanEntity subscriptionPlanExist(String planId) throws CommonException {
        Optional<SubscriptionPlanEntity> subscriptionPlanEntity = subscriptionPlanRepo.findByIdAndStatus(planId, EnumStatusType.ACTIVE);
        if (subscriptionPlanEntity.isEmpty()) {
            throw new CommonException("Plan Not Exist", HttpStatus.BAD_REQUEST.value());
        }
        return subscriptionPlanEntity.get();
    }

    public void validatePlanNameEdit(SubscriptionPlanEntity subscriptionPlanEntity) throws CommonException {
        String planName = subscriptionPlanEntity.getPlanName();
        EnumPlanType planType = subscriptionPlanEntity.getPlanType();
        String planId = subscriptionPlanEntity.getId();

        // Find any other plan with same name & type but different id
        Optional<SubscriptionPlanEntity> existingPlan = subscriptionPlanRepo
                .findByPlanNameAndPlanTypeAndStatusIn(
                        planName,
                        planType,
                        List.of(EnumStatusType.ACTIVE, EnumStatusType.INACTIVE)
                )
                .filter(plan -> !plan.getId().equals(planId)); // exclude current plan

        if (existingPlan.isPresent()) {
            log.error("❌ Duplicate plan name '{}' found for planType '{}' (Existing PlanId: {})", planName, planType, existingPlan.get().getId());
            throw new CommonException(
                    "Plan name already exists for this plan type!",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        log.debug("✅ Plan name '{}' is unique for plan type '{}'.", planName, planType);
    }

    public FeedbackEntity feedbackExist(String feedbackId) throws CommonException {
        return feedbackRepo.findByIdAndStatusNot(feedbackId, EnumStatusType.DELETE)
                .orElseThrow(() -> new CommonException("Feedback not found or deleted", HttpStatus.NO_CONTENT.value()));
    }

    public void validateUserAlreadySubscribed(UserEntity user,
                                              SubscriptionPlanEntity plan) throws CommonException {

        log.info("Checking if user already subscribed to this plan");

        Optional<SubscriberEntity> existing =
                subscriberRepo.findByUserAndPlanAndCurrentSubStatus(
                        user,
                        plan,
                        EnumSubscriptionStatus.ACTIVE
                );

        if (existing.isPresent()) {

            SubscriberEntity sub = existing.get();

            // Convert String → LocalDate
            LocalDate startDate = LocalDate.parse(sub.getSubStartDate());
            LocalDate endDate = LocalDate.parse(sub.getSubEndDate());
            LocalDate today = LocalDate.now();

            // Check if today between start and end
            boolean isActiveRange =
                    (!today.isBefore(startDate)) &&
                            (!today.isAfter(endDate));

            if (isActiveRange) {
                throw new CommonException(
                        "User already has an active subscription for this plan",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
        }
    }


    public SubscriberEntity subscriberExist(UserEntity user, SubscriptionPlanEntity plan) throws CommonException {
        log.info("Validating existing subscription for user {}", user.getId());

        return subscriberRepo.findByUserAndPlanAndStatus(user, plan, EnumStatusType.ACTIVE)
                .orElseThrow(() -> new CommonException("Subscription not found", HttpStatus.BAD_REQUEST.value()));
    }

    public SubscriberEntity subscriptionExistById(String subscriptionId) throws CommonException {
        return subscriberRepo.findById(subscriptionId)
                .orElseThrow(() -> new CommonException("Subscription not found", HttpStatus.BAD_REQUEST.value()));
    }

    public void validateSubscriptionBelongsToUser(UserEntity user, SubscriberEntity subscriber) throws CommonException {
        if (!subscriber.getUser().getId().equals(user.getId())) {
            throw new CommonException("This subscription does not belong to the user",
                    HttpStatus.UNAUTHORIZED.value());
        }
    }

    public void validateSubscriptionRenewal(SubscriberEntity subscriber) throws CommonException {
        log.info("Validating renewal for subscription {}", subscriber.getId());

        if (subscriber.getCurrentSubStatus() == EnumSubscriptionStatus.CANCELLED) {
            throw new CommonException("Cancelled subscription cannot be renewed",
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    public void validateSubscriptionCancel(SubscriberEntity subscriber) throws CommonException {
        log.info("Validating cancellation for subscription {}", subscriber.getId());

        if (subscriber.getCurrentSubStatus() == EnumSubscriptionStatus.CANCELLED) {
            throw new CommonException("Subscription already cancelled",
                    HttpStatus.BAD_REQUEST.value());
        }

        if (subscriber.getCurrentSubStatus() == EnumSubscriptionStatus.EXPIRED) {
            throw new CommonException("Expired subscription cannot be cancelled",
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    public PaymentEntity getLatestPayment(String userId, String planId) throws CommonException {

        // 1️⃣ Validate User Exists
        UserEntity user = validateUserOrNot(userId);

        // 2️⃣ Validate Plan Exists
        SubscriptionPlanEntity plan = subscriptionPlanExist(planId);

        // 3️⃣ Build Query to fetch LATEST payment
        Query query = new Query();
        query.addCriteria(Criteria.where("user_id").is(user.getId()));
        query.addCriteria(Criteria.where("plan_id").is(plan.getId()));
        query.addCriteria(Criteria.where("payment_status").is(EnumPaymentStatus.SUCCESS));
        query.with(Sort.by(Sort.Direction.DESC, "created_at"));
        query.limit(1);

        // 4️⃣ Find latest payment
        PaymentEntity latestPayment = mongoTemplate.findOne(query, PaymentEntity.class);

        return latestPayment;  // null if no payments found
    }

}
