package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.SubscriptionPlanEntity;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

@Component
public interface SubscriptionPlanService {
    SubscriptionPlanEntity createPlan(String adminId, PlanCreateRequestDto planCreateRequestDto) throws CommonException;

    PlanDetailsDto getPlanDetails(String userId, String planId) throws CommonException;

    CommonPaginatedResponse<PlanDetailsDto> getPlanList(String userId, String search, String filterBy, String sortBy, String sortDir, int offset, int limit) throws CommonException;

    PlanDetailsDto editPlan(String userId, String targetPlanId, PlanEditRequestDto editPlanDto) throws CommonException;

    void deletePlan(String userId, String targetPlanId) throws CommonException;
}
