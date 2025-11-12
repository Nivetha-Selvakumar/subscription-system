package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.SubscriberEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberRepo extends MongoRepository<SubscriberEntity, String> {
    SubscriberEntity findByUser(UserEntity user);

    Optional<SubscriberEntity> findByUserAndStatus(UserEntity targetUser, EnumStatusType enumStatusType);
}
