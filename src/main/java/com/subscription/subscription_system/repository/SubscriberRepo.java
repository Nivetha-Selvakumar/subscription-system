package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.SubscriberEntity;
import com.subscription.subscription_system.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriberRepo extends MongoRepository<SubscriberEntity, String> {
    SubscriberEntity findByUser(UserEntity user);
}
