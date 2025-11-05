package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuthTokenRepo extends MongoRepository<AuthTokenEntity, String> {
    Optional<AuthTokenEntity> findByUserAndStatus(UserEntity user, String name);
}
