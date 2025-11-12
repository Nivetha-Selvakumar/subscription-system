package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.PaymentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends MongoRepository<PaymentEntity, String> {

}
