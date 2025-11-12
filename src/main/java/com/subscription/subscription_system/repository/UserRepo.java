package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<UserEntity, String> {

    // Find by email
    UserEntity findByEmail(String email);

    Optional<UserEntity> findByIdAndStatus(String userId, EnumStatusType enumStatusType);

    UserEntity findByEmailAndStatus(String email, EnumStatusType enumStatusType);
}
