package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepo extends MongoRepository<AdminEntity, String> {
    AdminEntity findByUser(UserEntity user);

    Optional<AdminEntity> findByUserAndStatus(UserEntity userEntity, EnumStatusType enumStatusType);
}
