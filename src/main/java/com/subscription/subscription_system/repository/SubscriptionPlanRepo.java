package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.SubscriptionPlanEntity;
import com.subscription.subscription_system.enumuration.EnumPlanType;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepo extends MongoRepository<SubscriptionPlanEntity, String> {

    SubscriptionPlanEntity findByPlanNameAndPlanTypeAndStatus(String planName, EnumPlanType enumPlanType, EnumStatusType enumStatusType);

    Optional<SubscriptionPlanEntity> findByIdAndStatus(String planId, EnumStatusType enumStatusType);

    Optional<SubscriptionPlanEntity> findByPlanNameAndPlanType(String planName, EnumPlanType planType);

    Optional<SubscriptionPlanEntity> findByPlanNameAndPlanTypeAndStatusIn(String planName, EnumPlanType planType, List<EnumStatusType> active);
}
