package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.CommonPaginatedResponse;
import com.subscription.subscription_system.dto.PlanCreateRequestDto;
import com.subscription.subscription_system.dto.PlanDetailsDto;
import com.subscription.subscription_system.dto.PlanEditRequestDto;
import com.subscription.subscription_system.entity.SubscriptionPlanEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
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

import java.time.LocalDateTime;
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
        log.info("Create Plan API invoked by adminId: {}", adminId);
        UserEntity adminUser = businessValidation.getAdminByUserId(adminId);

        // Check admin user or not
        log.info("Create Plan Checking Admin User Or Not");
        businessValidation.checkAdminOrNot(adminUser);

        // Existing plan name or not for the plan type
        log.info("Create Plan Checking Existing plan Or Not");
        businessValidation.validateSubscriptionNameExist(planCreateRequestDto.getPlanName(), planCreateRequestDto.getPlanType());

        // Map Plan Details
        log.info("Mapping plan to entity");
        SubscriptionPlanEntity subscriptionPlanEntity = subscriptionPlanMapper.mapToSubscriberPlanEntity(planCreateRequestDto.getPlanName(), planCreateRequestDto.getPlanType(),
                planCreateRequestDto.getPlanCost(), planCreateRequestDto.getDescription(), EnumStatusType.ACTIVE,
                adminUser.getFirstName() + " " + adminUser.getLastName());

        log.info("Saving the plan details to repo");
        subscriptionPlanRepo.save(subscriptionPlanEntity);

        log.info("Saving Plan Completed");
        return subscriptionPlanEntity;
    }

    @Override
    public PlanDetailsDto getPlanDetails(String userId, String planId) throws CommonException {

        //User or Not
        log.info("Get Plan details api invoked and Checking user or not");
        businessValidation.validateUserOrNot(userId);

        log.info("Get plan details check subscription plan exist or not");
        SubscriptionPlanEntity subscriptionPlanEntity = businessValidation.subscriptionPlanExist(planId);

        log.info("Get plan details Map to dto");
        return subscriptionPlanMapper.mapPlanEntityToDto(subscriptionPlanEntity);

    }

    @Override
    public CommonPaginatedResponse<PlanDetailsDto> getPlanList(String userId, String search, String filterBy, String sortBy, String sortDir, int offset, int limit) throws CommonException {
        // 1️⃣ Validate User
        log.info("Get Plan List api invoked and Checking user or not");
        businessValidation.validateUserOrNot(userId);

        // 2️⃣ Build Main Query
        log.info("Get Plan List api Building Query");
        Query query = QueryUtils.buildPlanQuery(search, filterBy, sortBy, sortDir);

        // 3️⃣ Get total count BEFORE pagination
        log.info("Get Plan List api total count from query");
        long totalCount = mongoTemplate.count(query, SubscriptionPlanEntity.class);

        // 4️⃣ Apply pagination
        log.info("Get Plan List api apply pagination");
        query.skip(offset).limit(limit);

        // 5️⃣ Fetch paginated plans
        log.info("Get Plan List api Fetch Plans");
        List<SubscriptionPlanEntity> subscriptionPlanEntityList = mongoTemplate.find(query, SubscriptionPlanEntity.class);

        // 6️⃣ Map entities to DTOs
        log.info("Get Plan List api map to Dto");
        List<PlanDetailsDto> planDetailsDtoList = subscriptionPlanEntityList.stream()
                .map(plan -> subscriptionPlanMapper.mapPlanEntityToDto(plan))
                .toList();

        // 7️⃣ Return paginated response
        log.info("Get Plan List api Completed");
        return new CommonPaginatedResponse<>(planDetailsDtoList, totalCount);
    }

    @Override
    public PlanDetailsDto editPlan(String adminUserId, String targetPlanId, PlanEditRequestDto editPlanDto) throws CommonException {
        // Check User or not
        log.info("Edit Plan api invoked and Checking user or not");
        UserEntity adminUser = businessValidation.getAdminByUserId(adminUserId);

        // Check admin user or not
        log.info("Edit Plan api check admin or not");
        businessValidation.checkAdminOrNot(adminUser);

        log.info("Edit Plan api Plan exist or not");
        SubscriptionPlanEntity subscriptionPlanEntity = businessValidation.subscriptionPlanExist(targetPlanId);

        log.info("Edit Plan Api plan name exist other than this id or not");
        businessValidation.validatePlanNameEdit(subscriptionPlanEntity);

        log.info("Edit Plan api Mapping to entity");
        subscriptionPlanMapper.mapEditToSubscriberPlanEntity(editPlanDto.getPlanName(), editPlanDto.getPlanType(),
                editPlanDto.getPlanCost(), editPlanDto.getDescription(),
                EnumStatusType.fromValue(editPlanDto.getStatus()), adminUserId, subscriptionPlanEntity);

        log.info("Edit Plan api saving to repo");
        subscriptionPlanRepo.save(subscriptionPlanEntity);

        log.info("Edit Plan api map to Dto");
        return subscriptionPlanMapper.mapPlanEntityToDto(subscriptionPlanEntity);
    }

    @Override
    public void deletePlan(String adminUserId, String targetPlanId) throws CommonException {
        // Check User or not
        log.info("Delete Plan api invoked and Checking user or not");
        UserEntity adminUser = businessValidation.getAdminByUserId(adminUserId);

        // Check admin user or not
        log.info("Delete Plan api Admin or not");
        businessValidation.checkAdminOrNot(adminUser);

        log.info("Delete Plan api Plan exist or not");
        SubscriptionPlanEntity subscriptionPlanEntity = businessValidation.subscriptionPlanExist(targetPlanId);

        log.info("Delete Plan api Status change");
        subscriptionPlanEntity.setStatus(EnumStatusType.DELETE);
        subscriptionPlanEntity.setUpdatedAt(LocalDateTime.now());
        subscriptionPlanEntity.setUpdatedBy(adminUser.getFirstName() + " " + adminUser.getLastName());

        log.info("Delete Plan api saving repo");
        subscriptionPlanRepo.save(subscriptionPlanEntity);

        log.info("✅ Plan deletion completed for targetPlanId: {}", targetPlanId);
    }
}
