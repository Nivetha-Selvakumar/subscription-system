package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.SubscriberEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriberRepo extends MongoRepository<SubscriberEntity, String> {
}
