package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.CommonPaginatedResponse;
import com.subscription.subscription_system.dto.PaginatedResponse;
import com.subscription.subscription_system.dto.PlanCreateRequestDto;
import com.subscription.subscription_system.dto.PlanDetailsDto;
import com.subscription.subscription_system.entity.SubscriptionPlanEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.SubscriptionPlanMapper;
import com.subscription.subscription_system.repository.SubscriptionPlanRepo;
import com.subscription.subscription_system.service.SubscriptionPlanService;
import com.subscription.subscription_system.utils.QueryUtils;
import com.subscription.subscription_system.validation.BusinessValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SubscriptionPlanImpl implements SubscriptionPlanService {

    @Autowired
    BusinessValidation businessValidation;

    @Autowired
    SubscriptionPlanRepo subscriptionPlanRepo;

    @Autowired
    SubscriptionPlanMapper subscriptionPlanMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public SubscriptionPlanEntity createPlan(String adminId, PlanCreateRequestDto planCreateRequestDto) throws CommonException {
        // User Of Admin Exist or not
        UserEntity adminUser = businessValidation.getAdminByUserId(adminId);

        // Check admin user or not
        businessValidation.checkAdminOrNot(adminUser);

        // Existing plan name or not for the plan type
        businessValidation.validateSubscriptionNameExist(planCreateRequestDto.getPlanName(), planCreateRequestDto.getPlanType());

        // Map Plan Details
        SubscriptionPlanEntity subscriptionPlanEntity = subscriptionPlanMapper.mapToSubscriberPlanEntity(planCreateRequestDto.getPlanName(), planCreateRequestDto.getPlanType(),
                planCreateRequestDto.getPlanCost(), planCreateRequestDto.getDescription(),adminUser.getFirstName() + " " + adminUser.getLastName());

        subscriptionPlanRepo.save(subscriptionPlanEntity);

        return subscriptionPlanEntity;
    }

    @Override
    public PlanDetailsDto getPlanDetails(String userId, String planId) throws CommonException {

        //User or Not
        businessValidation.validateUserOrNot(userId);

        SubscriptionPlanEntity subscriptionPlanEntity = businessValidation.subscriptionPlanExist(planId);

        return subscriptionPlanMapper.mapPlanEntityToDto(subscriptionPlanEntity);

    }

    @Override
    public CommonPaginatedResponse<PlanDetailsDto> getPlanList(String userId, String search, String filterBy, String sortBy, String sortDir, int offset, int limit) throws CommonException {
        // 1️⃣ Validate User
        businessValidation.validateUserOrNot(userId);

        // 2️⃣ Build Main Query
        Query query = QueryUtils.buildPlanQuery(search, filterBy, sortBy, sortDir);

        // 3️⃣ Get total count BEFORE pagination
        long totalCount = mongoTemplate.count(query, SubscriptionPlanEntity.class);

        // 4️⃣ Apply pagination
        query.skip(offset).limit(limit);

        // 5️⃣ Fetch paginated plans
        List<SubscriptionPlanEntity> subscriptionPlanEntityList = mongoTemplate.find(query, SubscriptionPlanEntity.class);

        // 6️⃣ Map entities to DTOs
        List<PlanDetailsDto> planDetailsDtoList = subscriptionPlanEntityList.stream()
                .map(plan -> subscriptionPlanMapper.mapPlanEntityToDto(plan))
                .toList();

        // 7️⃣ Return paginated response
        return new CommonPaginatedResponse<>(planDetailsDtoList, totalCount);
    }
}
