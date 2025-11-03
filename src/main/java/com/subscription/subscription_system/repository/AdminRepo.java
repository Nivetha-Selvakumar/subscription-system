package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.AdminEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdminRepo extends MongoRepository<AdminEntity, String> {
}
