package com.subscription.subscription_system.repository;

import com.subscription.subscription_system.entity.SupportTicketEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumTicketStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepo extends MongoRepository<SupportTicketEntity, String> {
    Optional<SupportTicketEntity> findByIdAndStatusNot(String targetTicketId, EnumStatusType enumStatusType);

    long countByTicketStatusInAndStatusNot(List<EnumTicketStatus> statuses, EnumStatusType status);

    List<SupportTicketEntity> findTop5ByOrderByCreatedAtDesc();
}
