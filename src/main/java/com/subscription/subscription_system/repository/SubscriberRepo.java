package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.SubscriberEntity;
import com.subscription.subscription_system.entity.SubscriptionPlanEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumSubscriptionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepo extends MongoRepository<SubscriberEntity, String> {
    SubscriberEntity findByUser(UserEntity user);

    Optional<SubscriberEntity> findByUserAndStatus(UserEntity targetUser, EnumStatusType enumStatusType);

    Optional<SubscriberEntity> findByUserAndPlanAndCurrentSubStatus(UserEntity user, SubscriptionPlanEntity plan, EnumSubscriptionStatus enumSubscriptionStatus);

    Optional<SubscriberEntity> findByUserAndPlanAndStatus(UserEntity user, SubscriptionPlanEntity plan, EnumStatusType enumStatusType);

    long countByCurrentSubStatus(EnumSubscriptionStatus enumSubscriptionStatus);

    List<SubscriberEntity> findTop5ByOrderByCreatedAtDesc();

    List<SubscriberEntity> findAllByCurrentSubStatusNotInAndSubStartDateBetween(List<EnumSubscriptionStatus> excludedStatus, String start, String today);

    List<SubscriberEntity> findTop5ByUserIdOrderByCreatedAtDesc(String userId);

    SubscriberEntity findTopByUserOrderByCreatedAtDesc(UserEntity user);

    List<SubscriberEntity> findByStatusNot(EnumStatusType enumStatusType);

    SubscriberEntity findByUserAndStatusNotAndCurrentSubStatusNotIn(UserEntity user, EnumStatusType enumStatusType, List<EnumSubscriptionStatus> subStatus);
}
