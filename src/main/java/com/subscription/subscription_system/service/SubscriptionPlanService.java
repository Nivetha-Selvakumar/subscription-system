package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.CommonPaginatedResponse;
import com.subscription.subscription_system.dto.PaginatedResponse;
import com.subscription.subscription_system.dto.PlanCreateRequestDto;
import com.subscription.subscription_system.dto.PlanDetailsDto;
import com.subscription.subscription_system.entity.SubscriptionPlanEntity;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

@Component
public interface SubscriptionPlanService {
    SubscriptionPlanEntity createPlan(String adminId, PlanCreateRequestDto planCreateRequestDto) throws CommonException;

    PlanDetailsDto getPlanDetails(String userId, String planId) throws CommonException;

    CommonPaginatedResponse<PlanDetailsDto> getPlanList(String userId, String search, String filterBy, String sortBy, String sortDir, int offset, int limit) throws CommonException;
}
