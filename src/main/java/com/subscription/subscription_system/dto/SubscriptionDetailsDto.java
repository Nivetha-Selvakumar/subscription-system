package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDetailsDto {

    private String subscriptionId;

    private String planId;
    private String planName;
    private String planType;
    private Double cost;

    private String currentSubStatus;

    private String subStartDate;
    private String subEndDate;

    private Double lastPaidAmount;
    private String lastPaymentStatus;
    private String lastPaymentDate;
}
