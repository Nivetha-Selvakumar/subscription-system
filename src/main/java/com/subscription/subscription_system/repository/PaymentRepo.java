package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.PaymentEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumPaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends MongoRepository<PaymentEntity, String> {

    List<PaymentEntity> countByPaymentStatusAndPaymentDateBetween(EnumPaymentStatus enumPaymentStatus, String startDate, String endDate);

    List<PaymentEntity> findTop5ByOrderByCreatedAtDesc();

    List<PaymentEntity> findByUserIdAndPaymentStatus(String userId, EnumPaymentStatus enumPaymentStatus);

    PaymentEntity findTopByUserAndPaymentStatusOrderByCreatedAtAsc(UserEntity user, EnumPaymentStatus enumPaymentStatus);

    List<PaymentEntity> findTop5ByUserIdOrderByCreatedAtDesc(String userId);
}
