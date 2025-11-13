package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.FeedbackEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepo extends MongoRepository<FeedbackEntity, String> {

    Optional<FeedbackEntity> findByIdAndStatusNot(String feedbackId, EnumStatusType enumStatusType);

    Optional<FeedbackEntity> findByUserAndStatusNot(UserEntity user, EnumStatusType enumStatusType);
}
