package com.subscription.subscription_system.mapper;

import com.subscription.subscription_system.dto.PlanDetailsDto;
import com.subscription.subscription_system.entity.SubscriptionPlanEntity;
import com.subscription.subscription_system.enumuration.EnumPlanType;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SubscriptionPlanMapper {

    public SubscriptionPlanEntity mapToSubscriberPlanEntity(String planName, String planType, String planCost,String desc,EnumStatusType statusType, String adminName) {
        SubscriptionPlanEntity subscriptionPlanEntity = new SubscriptionPlanEntity();
        subscriptionPlanEntity.setPlanName(planName);
        subscriptionPlanEntity.setPlanType(EnumPlanType.fromValue(planType.toUpperCase()));
        subscriptionPlanEntity.setCost(Double.valueOf(planCost));
        subscriptionPlanEntity.setDescription(desc);
        subscriptionPlanEntity.setStatus(statusType);
        subscriptionPlanEntity.setCreatedBy(adminName);
        subscriptionPlanEntity.setUpdatedBy(adminName);
        subscriptionPlanEntity.setCreatedAt(LocalDateTime.now());
        subscriptionPlanEntity.setUpdatedAt(LocalDateTime.now());

        return subscriptionPlanEntity;
    }

    public PlanDetailsDto mapPlanEntityToDto(SubscriptionPlanEntity subscriptionPlanEntity) {
        PlanDetailsDto planDetailsDto = new PlanDetailsDto();
        planDetailsDto.setId(subscriptionPlanEntity.getId());
        planDetailsDto.setPlanName(subscriptionPlanEntity.getPlanName());
        planDetailsDto.setPlanType(subscriptionPlanEntity.getPlanType().getValue());
        planDetailsDto.setPlanCost(subscriptionPlanEntity.getCost().toString());
        planDetailsDto.setDescription(subscriptionPlanEntity.getDescription());
        planDetailsDto.setStatus(subscriptionPlanEntity.getStatus().getName());
        planDetailsDto.setCreatedBy(subscriptionPlanEntity.getCreatedBy());
        planDetailsDto.setUpdatedBy(subscriptionPlanEntity.getUpdatedBy());
        planDetailsDto.setCreatedAt(subscriptionPlanEntity.getCreatedAt().toString());
        planDetailsDto.setUpdatedAt(subscriptionPlanEntity.getUpdatedAt().toString());

        return planDetailsDto;
    }

    public void mapEditToSubscriberPlanEntity(String planName, String planType, String planCost,
                                              String desc, EnumStatusType statusType,
                                              String adminName, SubscriptionPlanEntity subscriptionPlanEntity) {
        subscriptionPlanEntity.setPlanName(planName);
        subscriptionPlanEntity.setPlanType(EnumPlanType.fromValue(planType.toUpperCase()));
        subscriptionPlanEntity.setCost(Double.valueOf(planCost));
        subscriptionPlanEntity.setDescription(desc);
        subscriptionPlanEntity.setStatus(statusType);
        subscriptionPlanEntity.setUpdatedBy(adminName);
        subscriptionPlanEntity.setUpdatedAt(LocalDateTime.now());

    }
}

