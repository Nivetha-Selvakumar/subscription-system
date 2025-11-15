package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.CommonPaginatedResponse;
import com.subscription.subscription_system.dto.SubscriptionCreateDto;
import com.subscription.subscription_system.dto.SubscriptionDetailsDto;
import com.subscription.subscription_system.dto.SubscriptionEditDto;
import com.subscription.subscription_system.entity.PaymentEntity;
import com.subscription.subscription_system.entity.SubscriberEntity;
import com.subscription.subscription_system.entity.SubscriptionPlanEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumSubscriptionStatus;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.SubscriptionMapper;
import com.subscription.subscription_system.repository.PaymentRepo;
import com.subscription.subscription_system.repository.SubscriberRepo;
import com.subscription.subscription_system.repository.SubscriptionPlanRepo;
import com.subscription.subscription_system.service.SubscriptionService;
import com.subscription.subscription_system.utils.QueryUtils;
import com.subscription.subscription_system.validation.BusinessValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class SubscriptionImpl implements SubscriptionService {
    @Autowired
    BusinessValidation businessValidation;

    @Autowired
    SubscriberRepo subscriberRepo;

    @Autowired
    SubscriptionPlanRepo subscriptionPlanRepo;

    @Autowired
    PaymentRepo paymentRepo;

    @Autowired
    SubscriptionMapper subscriptionMapper;

    @Autowired
    MongoTemplate mongoTemplate;

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

        // 2️⃣ Validate Plan existence
        SubscriptionPlanEntity planEntity = businessValidation.subscriptionPlanExist(planId);

        // 3️⃣ Check duplicate active subscription
        businessValidation.validateUserAlreadySubscribed(userEntity, planEntity);

        // 4️⃣ Create Subscriber entry
        log.info("Mapping subscription to SubscriberEntity");
        SubscriberEntity subscriber = subscriptionMapper.mapToNewSubscriber(
                userEntity,
                planEntity,
                subscriptionCreateDto
        );
        subscriberRepo.save(subscriber);

        // 5️⃣ Store a Payment Entry
        log.info("Saving Payment details");
        PaymentEntity payment = subscriptionMapper.mapToInitialPayment(
                userEntity,
                planEntity,
                subscriptionCreateDto
        );
        paymentRepo.save(payment);

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

        // 1️⃣ Validate User
        UserEntity userEntity = businessValidation.validateUserOrNot(userId);

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
                planEntity,
                subscriptionEditDto
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

        // 2️⃣ Validate Subscription Exists
        SubscriberEntity subscriber = businessValidation.subscriptionExistById(subscriptionId);

        // 3️⃣ Validate Ownership
        businessValidation.validateSubscriptionBelongsToUser(userEntity, subscriber);

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

        // 1️⃣ Validate User
        UserEntity userEntity = businessValidation.validateUserOrNot(userId);

        // 2️⃣ Build Query with user scope
        Query query = QueryUtils.buildSubscriptionQuery(
                userId,
                search,
                filterBy,
                sortBy,
                sortDir
        );

        // Total count
        long total = mongoTemplate.count(query, PaymentEntity.class);

        // Pagination
        query.skip(offset).limit(limit);

        List<PaymentEntity> payments = mongoTemplate.find(query, PaymentEntity.class);

        List<SubscriptionDetailsDto> dtoList = payments.stream()
                .map(payment -> subscriptionMapper.mapPaymentToSubscriptionDto(payment))
                .toList();

        return new CommonPaginatedResponse<>(dtoList, total);
    }


}
