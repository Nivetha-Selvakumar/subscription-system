package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.SupportTicketEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupportTicketRepo extends MongoRepository<SupportTicketEntity, String> {
    Optional<SupportTicketEntity> findByIdAndStatusNot(String targetTicketId, EnumStatusType enumStatusType);
}
