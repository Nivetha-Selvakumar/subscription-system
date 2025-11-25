package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

@Component
public interface SubscriptionService {
    SubscriptionDetailsDto createSubscription(String userId, String planId, SubscriptionCreateDto subscriptionCreateDto) throws CommonException;

    SubscriptionDetailsDto editSubscription(String userId, String planId, SubscriptionEditDto subscriptionEditDto) throws CommonException;

    SubscriptionDetailsDto cancelSubscription(String userId, String planId) throws CommonException;

    SubscriptionDetailsDto getSubscriptionDetails(String userId, String targetSubscriptionId) throws CommonException;

    CommonPaginatedResponse<SubscriptionDetailsDto> getSubscriptionPaymentList(String userId, String search, String filterBy, String sortBy, String sortDir, int offset, int limit) throws CommonException;

    ExpiredResultDto expireSubscriptions() throws CommonException;
}
