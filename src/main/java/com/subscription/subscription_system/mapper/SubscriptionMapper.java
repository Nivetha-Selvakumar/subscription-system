package com.subscription.subscription_system.mapper;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.*;
import com.subscription.subscription_system.enumuration.EnumPaymentStatus;
import com.subscription.subscription_system.enumuration.EnumPlanType;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumSubscriptionStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class SubscriptionMapper {

    // ------------------------------------------------------
    // CREATE SUBSCRIBER MAPPING
    // ------------------------------------------------------
    public SubscriberEntity mapToNewSubscriber(
            UserEntity user,
            SubscriptionPlanEntity plan,
            SubscriptionCreateDto dto
    ) {

        SubscriberEntity subscriber = new SubscriberEntity();

        subscriber.setUser(user);
        subscriber.setPlan(plan);

        subscriber.setStatus(EnumStatusType.ACTIVE);
        subscriber.setCurrentSubStatus(EnumSubscriptionStatus.ACTIVE);

        // ---------- DATE CALCULATION FIXED ----------
        LocalDate start = LocalDate.now();
        LocalDate end;

        if (plan.getPlanType() == EnumPlanType.MONTHLY) {
            end = start.plusMonths(1);
        } else if (plan.getPlanType() == EnumPlanType.YEARLY) {
            end = start.plusYears(1);
        } else {
            // fallback (if future plan types are added)
            end = start.plusMonths(1);
        }

        subscriber.setSubStartDate(start.toString());
        subscriber.setSubEndDate(end.toString());
        subscriber.setJoinDate(start.toString());
        // ---------------------------------------------

        subscriber.setCreatedAt(LocalDateTime.now());
        subscriber.setCreatedBy(user.getFirstName() + " " + user.getLastName());

        subscriber.setUpdatedAt(LocalDateTime.now());
        subscriber.setUpdatedBy(user.getFirstName() + " " + user.getLastName());

        return subscriber;
    }


    // ------------------------------------------------------
    // RENEW SUBSCRIBER MAPPING
    // ------------------------------------------------------
    public void mapRenewalToSubscriber(
            SubscriberEntity subscriber,
            SubscriptionPlanEntity plan
    ) {
        subscriber.setPlan(plan);
        subscriber.setCurrentSubStatus(EnumSubscriptionStatus.ACTIVE);
        subscriber.setSubEndDate(LocalDate.now().plusMonths(1).toString());
        subscriber.setUpdatedAt(LocalDateTime.now());
    }

    // ------------------------------------------------------
    // INITIAL PAYMENT
    // ------------------------------------------------------
    public PaymentEntity mapToInitialPayment(
            UserEntity user,
            SubscriptionPlanEntity plan,
            SubscriptionCreateDto dto
    ) {

        PaymentEntity payment = new PaymentEntity();

        payment.setUser(user);
        payment.setPlan(plan);
        payment.setAmount(Double.valueOf(dto.getAmount()));
        payment.setPaymentStatus(EnumPaymentStatus.fromValue(dto.getPaymentStatus()));
        payment.setPaymentDate(LocalDate.now().toString());
        payment.setStatus(EnumStatusType.ACTIVE.getName());

        payment.setCreatedAt(LocalDateTime.now());
        payment.setCreatedBy(user.getFirstName() + " " + user.getLastName());

        return payment;
    }

    // ------------------------------------------------------
    // RENEW PAYMENT
    // ------------------------------------------------------
    public PaymentEntity mapToRenewalPayment(
            UserEntity user,
            SubscriptionPlanEntity plan,
            SubscriptionEditDto dto
    ) {

        PaymentEntity payment = new PaymentEntity();

        payment.setUser(user);
        payment.setPlan(plan);
        payment.setAmount(dto.getRenewAmount());
        payment.setPaymentStatus(EnumPaymentStatus.fromValue(dto.getPaymentStatus()));
        payment.setPaymentDate(LocalDate.now().toString());
        payment.setStatus(EnumStatusType.ACTIVE.getName());

        payment.setCreatedAt(LocalDateTime.now());
        payment.setCreatedBy(user.getFirstName() + " " + user.getLastName());

        return payment;
    }

    // ------------------------------------------------------
    // SUBSCRIPTION DETAILS DTO
    // ------------------------------------------------------
    public SubscriptionDetailsDto mapSubscriberToDetailsDto(
            SubscriberEntity subscriber,
            SubscriptionPlanEntity plan,
            PaymentEntity payment
    ) {
        SubscriptionDetailsDto dto = new SubscriptionDetailsDto();
        if (subscriber != null) {
            dto.setSubscriptionId(subscriber.getId());
            dto.setCurrentSubStatus(subscriber.getCurrentSubStatus().getName());
            dto.setSubStartDate(subscriber.getSubStartDate());
            dto.setSubEndDate(subscriber.getSubEndDate());
        } else {
            dto.setSubscriptionId(null);
            dto.setCurrentSubStatus(null);
            dto.setSubStartDate(null);
            dto.setSubEndDate(null);
        }

        dto.setPlanId(plan.getId());
        dto.setPlanName(plan.getPlanName());
        dto.setPlanType(plan.getPlanType().toString());
        dto.setCost(plan.getCost());


        dto.setLastPaidAmount(payment.getAmount());
        dto.setLastPaymentStatus(payment.getPaymentStatus().getValue());
        dto.setLastPaymentDate(payment.getPaymentDate());

        return dto;
    }

    // ------------------------------------------------------
    // BASIC DTO (after cancel)
    // ------------------------------------------------------
    public SubscriptionDetailsDto mapSubscriberToBasicDto(
            SubscriberEntity subscriber,
            SubscriptionPlanEntity plan
    ) {
        SubscriptionDetailsDto dto = new SubscriptionDetailsDto();
        dto.setSubscriptionId(subscriber.getId());
        dto.setPlanId(plan.getId());
        dto.setPlanName(plan.getPlanName());
        dto.setPlanType(plan.getPlanType().toString());
        dto.setCurrentSubStatus(subscriber.getCurrentSubStatus().getName());
        dto.setSubStartDate(subscriber.getSubStartDate());
        dto.setSubEndDate(subscriber.getSubEndDate());
        return dto;
    }

    // ===========================================================
    // MAP: Subscriber + Plan + Payment → SubscriptionDetailsDto
    // ===========================================================
    public SubscriptionDetailsDto mapSubscriberToBasicDto(
            SubscriberEntity subscriber,
            SubscriptionPlanEntity plan,
            PaymentEntity latestPayment
    ) {
        SubscriptionDetailsDto dto = new SubscriptionDetailsDto();

        dto.setSubscriptionId(subscriber.getId());

        // ---- PLAN DETAILS ----
        if (plan != null) {
            dto.setPlanId(plan.getId());
            dto.setPlanName(plan.getPlanName());
            dto.setPlanType(plan.getPlanType() != null ? plan.getPlanType().getValue() : null);
            dto.setCost(plan.getCost());
        }

        // ---- SUBSCRIBER DETAILS ----
        dto.setCurrentSubStatus(
                subscriber.getCurrentSubStatus() != null ? subscriber.getCurrentSubStatus().getValue() : null
        );
        dto.setSubStartDate(subscriber.getSubStartDate());
        dto.setSubEndDate(subscriber.getSubEndDate());

        // ---- LAST PAYMENT ----
        if (latestPayment != null) {
            dto.setLastPaidAmount(latestPayment.getAmount());
            dto.setLastPaymentStatus(latestPayment.getPaymentStatus().getValue());
            dto.setLastPaymentDate(latestPayment.getPaymentDate());
        }

        return dto;
    }

    // ===========================================================
    // MAP PAYMENT → SubscriptionDetailsDto  (List View)
    // ===========================================================
    public SubscriptionDetailsDto mapPaymentListDto(PaymentEntity payment,
                                                    SubscriberEntity subscriber) {

        SubscriptionDetailsDto dto = new SubscriptionDetailsDto();

        SubscriptionPlanEntity plan = payment.getPlan();

        dto.setSubscriptionId(subscriber != null ? subscriber.getId() : null);
        dto.setPlanId(plan != null ? plan.getId() : null);
        dto.setPlanName(plan != null ? plan.getPlanName() : null);
        dto.setPlanType(plan != null ? plan.getPlanType().getValue() : null);
        dto.setCost(plan != null ? plan.getCost() : null);

        // Subscriber fields
        dto.setCurrentSubStatus(subscriber != null && subscriber.getCurrentSubStatus() != null
                ? subscriber.getCurrentSubStatus().getValue()
                : "-");

        dto.setSubStartDate(subscriber != null ? subscriber.getSubStartDate() : null);
        dto.setSubEndDate(subscriber != null ? subscriber.getSubEndDate() : null);

        // Payment fields
        dto.setLastPaidAmount(payment.getAmount());
        dto.setLastPaymentStatus(payment.getPaymentStatus().getValue());
        dto.setLastPaymentDate(payment.getPaymentDate());

        return dto;
    }


    public SubscriberEntity mapToUpdateSubscriber(
            SubscriberEntity subscriber,
            SubscriptionPlanEntity plan
    ) {
        // Update plan
        subscriber.setPlan(plan);

        // Update subscription status
        subscriber.setStatus(EnumStatusType.ACTIVE);
        subscriber.setCurrentSubStatus(EnumSubscriptionStatus.ACTIVE);

        // -------- DATE EXTEND LOGIC ----------
        LocalDate start = LocalDate.now();
        LocalDate end;

        if (plan.getPlanType() == EnumPlanType.MONTHLY) {
            end = start.plusMonths(1);
        } else if (plan.getPlanType() == EnumPlanType.YEARLY) {
            end = start.plusYears(1);
        } else {
            end = start.plusMonths(1);
        }

        subscriber.setSubStartDate(start.toString());
        subscriber.setSubEndDate(end.toString());
//        subscriber.setJoinDate(start.toString()); No need to update
        // --------------------------------------

        subscriber.setUpdatedAt(LocalDateTime.now());
        subscriber.setUpdatedBy(subscriber.getUser().getFirstName() + " " + subscriber.getUser().getLastName());

        return subscriber;
    }

}
