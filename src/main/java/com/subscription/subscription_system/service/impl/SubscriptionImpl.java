package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.*;
import com.subscription.subscription_system.enumuration.EnumPaymentStatus;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumSubscriptionStatus;
import com.subscription.subscription_system.enumuration.EnumUserType;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.SubscriptionMapper;
import com.subscription.subscription_system.repository.*;
import com.subscription.subscription_system.service.SubscriptionService;
import com.subscription.subscription_system.utils.QueryUtils;
import com.subscription.subscription_system.validation.BusinessValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SubscriptionImpl implements SubscriptionService {
    @Autowired
    BusinessValidation businessValidation;

    @Autowired
    SubscriberRepo subscriberRepo;

    @Autowired
    AdminRepo adminRepo;

    @Autowired
    PaymentRepo paymentRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    SubscriptionMapper subscriptionMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    EmailServiceImpl emailService;

    // -------------------------------------------------------
    //  CREATE SUBSCRIPTION
    // -------------------------------------------------------
    @Override
    public SubscriptionDetailsDto createSubscription(
            String userId,
            String planId,
            SubscriptionCreateDto subscriptionCreateDto
    ) throws CommonException {

        log.info("Create Subscription API invoked by userId: {}", userId);

        // 1️⃣ Validate User existence
        UserEntity userEntity = businessValidation.validateUserOrNot(userId);

        // check not a admin
        Optional<AdminEntity> existingAdmin = adminRepo.findByUserAndStatus(userEntity, EnumStatusType.ACTIVE);

        if (existingAdmin.isPresent()) {
            throw new CommonException("Admin Cannot subscribe a plan", HttpStatus.BAD_REQUEST.value());
        }

        // 2️⃣ Validate Plan existence
        SubscriptionPlanEntity planEntity = businessValidation.subscriptionPlanExist(planId);

        // 3️⃣ Check duplicate active subscription
        businessValidation.validateUserAlreadySubscribed(userEntity, planEntity);

        //  4️⃣Store a Payment Entry
        log.info("Saving Payment details");

        PaymentEntity payment = subscriptionMapper.mapToInitialPayment(
                userEntity,
                planEntity,
                subscriptionCreateDto
        );
        paymentRepo.save(payment);

        SubscriberEntity subscriber = null;

        if (subscriptionCreateDto.getPaymentStatus().equalsIgnoreCase(EnumPaymentStatus.SUCCESS.getValue())) {
            // 5️ Create Subscriber entry
            log.info("Mapping subscription to SubscriberEntity");
            subscriber = subscriptionMapper.mapToNewSubscriber(
                    userEntity,
                    planEntity,
                    subscriptionCreateDto
            );
            subscriberRepo.save(subscriber);

            userEntity.setRole(EnumUserType.SUBSCRIBER);
            userEntity.setUpdatedAt(LocalDateTime.now());
            userRepo.save(userEntity);
        }

        // 6️⃣ Return DTO
        return subscriptionMapper.mapSubscriberToDetailsDto(subscriber, planEntity, payment);
    }

    // -------------------------------------------------------
    //  RENEW / EXTEND SUBSCRIPTION
    // -------------------------------------------------------
    @Override
    public SubscriptionDetailsDto editSubscription(
            String userId,
            String planId,
            SubscriptionEditDto subscriptionEditDto
    ) throws CommonException {

        log.info("Renew Subscription API invoked by userId: {}", userId);
        // 1️⃣ Validate User existence
        UserEntity userEntity = businessValidation.validateUserOrNot(userId);

        // check not a admin
        Optional<AdminEntity> existingAdmin = adminRepo.findByUserAndStatus(userEntity, EnumStatusType.ACTIVE);

        if (existingAdmin.isPresent()) {
            throw new CommonException("Admin Cannot subscribe a plan", HttpStatus.BAD_REQUEST.value());
        }

        // 2️⃣ Validate Plan
        SubscriptionPlanEntity planEntity = businessValidation.subscriptionPlanExist(planId);

        // 3️⃣ Validate Subscription Exists
        SubscriberEntity subscriberEntity = businessValidation.subscriberExist(userEntity, planEntity);

        // 4️⃣ Validate subscription can be renewed
        businessValidation.validateSubscriptionRenewal(subscriberEntity);

        // 5️⃣ Update Renewal Fields
        log.info("Updating Subscriber renewal details...");
        subscriptionMapper.mapRenewalToSubscriber(
                subscriberEntity,
                planEntity
        );
        subscriberRepo.save(subscriberEntity);

        // 6️⃣ Store Renewal Payment
        PaymentEntity payment = subscriptionMapper.mapToRenewalPayment(
                userEntity,
                planEntity,
                subscriptionEditDto
        );
        paymentRepo.save(payment);

        // 7️⃣ Return DTO
        return subscriptionMapper.mapSubscriberToDetailsDto(subscriberEntity, planEntity, payment);
    }

    // -------------------------------------------------------
    //  CANCEL SUBSCRIPTION
    // -------------------------------------------------------
    @Override
    public SubscriptionDetailsDto cancelSubscription(String userId, String planId) throws CommonException {

        log.info("Cancel Subscription API invoked by userId: {}", userId);

        // 1️⃣ Validate User
        UserEntity userEntity = businessValidation.validateUserOrNot(userId);

        // check not a admin
        Optional<AdminEntity> existingAdmin = adminRepo.findByUserAndStatus(userEntity, EnumStatusType.ACTIVE);

        if (existingAdmin.isPresent()) {
            throw new CommonException("Admin Cannot subscribe a plan", HttpStatus.BAD_REQUEST.value());
        }

        // 2️⃣ Validate Plan
        SubscriptionPlanEntity planEntity = businessValidation.subscriptionPlanExist(planId);

        // 3️⃣ Validate Subscription Exists
        SubscriberEntity subscriber = businessValidation.subscriberExist(userEntity, planEntity);

        // 4️⃣ Validate cancellation allowed
        businessValidation.validateSubscriptionCancel(subscriber);

        // 5️⃣ Cancel Subscription
        subscriber.setCurrentSubStatus(EnumSubscriptionStatus.CANCELLED);
        subscriber.setStatus(EnumStatusType.INACTIVE);
        subscriber.setUpdatedAt(LocalDateTime.now());
        subscriber.setUpdatedBy(userEntity.getFirstName() + " " + userEntity.getLastName());

        subscriberRepo.save(subscriber);

        userEntity.setRole(EnumUserType.USER);
        userEntity.setUpdatedAt(LocalDateTime.now());
        userRepo.save(userEntity);

        return subscriptionMapper.mapSubscriberToBasicDto(subscriber, planEntity);
    }

    // -------------------------------------------------------
    //  GET SUBSCRIPTION DETAILS
    // -------------------------------------------------------
    @Override
    public SubscriptionDetailsDto getSubscriptionDetails(String userId, String subscriptionId) throws CommonException {

        log.info("Fetching subscription details for subscriptionId: {}", subscriptionId);

        // 1️⃣ Validate User
        UserEntity userEntity = businessValidation.validateUserOrNot(userId);

        Optional<AdminEntity> existingAdmin = adminRepo.findByUserAndStatus(userEntity, EnumStatusType.ACTIVE);

        // 2️⃣ Validate Subscription Exists
        SubscriberEntity subscriber = businessValidation.subscriptionExistById(subscriptionId);

        // 3️⃣ Validate Ownership
        if (existingAdmin.isEmpty()) {
            businessValidation.validateSubscriptionBelongsToUser(userEntity, subscriber);
        }

        // 4️⃣ Get Plan
        SubscriptionPlanEntity plan = subscriber.getPlan();

        // 5️⃣ Get Latest Payment for this plan + user (optional)
        PaymentEntity payment = businessValidation.getLatestPayment(userId, plan.getId());

        return subscriptionMapper.mapSubscriberToBasicDto(subscriber, plan, payment);
    }


    // -------------------------------------------------------
    //  GET SUBSCRIPTION LIST
    // -------------------------------------------------------
    @Override
    public CommonPaginatedResponse<SubscriptionDetailsDto> getSubscriptionPaymentList(
            String userId,
            String search,
            String filterBy,
            String sortBy,
            String sortDir,
            int offset,
            int limit
    ) throws CommonException {

        log.info("Fetching Subscription payment list... userId={}", userId);

        UserEntity userEntity = businessValidation.validateUserOrNot(userId);

        Optional<AdminEntity> existingAdmin =
                adminRepo.findByUserAndStatus(userEntity, EnumStatusType.ACTIVE);

        String effectiveUserId = existingAdmin.isPresent() ? null : userId;

        Query query = QueryUtils.buildSubscriptionQuery(
                effectiveUserId,
                search,
                filterBy,
                sortBy,
                sortDir
        );

        long total = mongoTemplate.count(query, PaymentEntity.class);

        query.skip(offset).limit(limit);

        List<PaymentEntity> payments = mongoTemplate.find(query, PaymentEntity.class);

        // ⭐ NEW: Fetch subscriber entity also
        List<SubscriptionDetailsDto> dtoList = payments.stream()
                .map(payment -> {

                    SubscriberEntity subscriber =
                            subscriberRepo.findByUserAndPlanAndStatus(
                                    payment.getUser(),
                                    payment.getPlan(),
                                    EnumStatusType.ACTIVE
                            ).orElse(null);

                    return subscriptionMapper.mapPaymentListDto(
                            payment,
                            subscriber
                    );
                })
                .toList();

        return new CommonPaginatedResponse<>(dtoList, total);
    }

    public ExpiredResultDto expireSubscriptions() {

        LocalDate today = LocalDate.now();

        // Fetch only NOT-DELETE subscribers
        List<SubscriberEntity> subscribers = subscriberRepo.findByStatusNot(EnumStatusType.DELETE);

        int successCount = 0;
        List<String> failedUsers = new ArrayList<>();

        for (SubscriberEntity sub : subscribers) {
            try {

                // Missing date → skip this user
                if (sub.getSubEndDate() == null) {
                    failedUsers.add(sub.getUser().getId() + " - SubEndDate missing");
                    continue;
                }

                EnumSubscriptionStatus currentStatus = sub.getCurrentSubStatus();

                // Only ACTIVE or PENDING should be processed
                if (currentStatus != EnumSubscriptionStatus.ACTIVE &&
                        currentStatus != EnumSubscriptionStatus.PENDING) {
                    continue;
                }

                LocalDate endDate = LocalDate.parse(sub.getSubEndDate());

                if (endDate.isBefore(today)) {

                    // Expire subscription
                    sub.setCurrentSubStatus(EnumSubscriptionStatus.EXPIRED);
                    sub.setStatus(EnumStatusType.DELETE);
                    sub.setUpdatedAt(LocalDateTime.now());
                    sub.setUpdatedBy("System Auto-Cron");

                    subscriberRepo.save(sub);
                    successCount++;
                }

            } catch (Exception ex) {

                String userId = (sub.getUser() != null) ? sub.getUser().getId() : "UNKNOWN";

                failedUsers.add(userId + " - " + ex.getMessage());

                log.error("Cron Expiry Error on user {} : {}", userId, ex.getMessage());
            }
        }

        return new ExpiredResultDto(successCount, failedUsers);
    }

    public ExpiredResultDto notifyUsersBeforeExpiry() {

        LocalDate today = LocalDate.now();

        List<SubscriberEntity> subscribers = subscriberRepo.findByStatusNot(EnumStatusType.DELETE);

        int notifiedCount = 0;
        List<String> failedUsers = new ArrayList<>();

        for (SubscriberEntity sub : subscribers) {

            try {
                if (sub.getSubEndDate() == null) {
                    failedUsers.add(sub.getUser().getEmail() + " - End date missing");
                    continue;
                }

                if (sub.getCurrentSubStatus() != EnumSubscriptionStatus.ACTIVE) {
                    continue;
                }

                LocalDate endDate = LocalDate.parse(sub.getSubEndDate());
                long daysLeft = ChronoUnit.DAYS.between(today, endDate);

                // Only notify for 3,2,1 days left
                if (daysLeft == 3 || daysLeft == 2 || daysLeft == 1) {

                    // Prevent duplicate email
                    if (sub.getLastReminderDate() != null &&
                            sub.getLastReminderDate().equals(today.toString()) &&
                            sub.getLastReminderDaysLeftSent() == daysLeft) {

                        // Already sent today
                        continue;
                    }

                    // Send email
                    String email = sub.getUser().getEmail();
                    String name = sub.getUser().getFirstName() + " " + sub.getUser().getLastName();

                    emailService.sendExpiryWarningEmail(email, name, endDate, daysLeft);

                    // Save reminder log
                    sub.setLastReminderDate(today.toString());
                    sub.setLastReminderDaysLeftSent((int) daysLeft);

                    subscriberRepo.save(sub);

                    notifiedCount++;
                }

            } catch (Exception ex) {

                failedUsers.add(sub.getUser() != null ? sub.getUser().getId() : "UNKNOWN");
                log.error("Reminder Cron Error for {}: {}", sub.getId(), ex.getMessage());
            }
        }

        return new ExpiredResultDto(notifiedCount, failedUsers);
    }


}
