package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.SupportResponseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportResponseRepo extends MongoRepository<SupportResponseEntity, String> {
}
