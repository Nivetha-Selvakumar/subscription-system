package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdminRepo extends MongoRepository<AdminEntity, String> {
    AdminEntity findByUser(UserEntity user);

    Optional<AdminEntity> findByUserAndStatus(UserEntity userEntity, EnumStatusType enumStatusType);
}
