package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdminRepo extends MongoRepository<AdminEntity, String> {
    AdminEntity findByUser(UserEntity user);
}
