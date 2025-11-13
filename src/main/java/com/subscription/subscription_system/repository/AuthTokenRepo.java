package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.AuthTokenEntity;
import com.subscription.subscription_system.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthTokenRepo extends MongoRepository<AuthTokenEntity, String> {
    Optional<AuthTokenEntity> findByUserAndStatus(UserEntity user, String name);

    List<AuthTokenEntity> findAllByUserAndStatus(UserEntity user, String name);
}
