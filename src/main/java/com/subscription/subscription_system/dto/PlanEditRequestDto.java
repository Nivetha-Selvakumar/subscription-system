package com.subscription.subscription_system.dto;

import lombok.Data;

@Data
public class PlanEditRequestDto {
    private String planName;
    private String planType;
    private String planCost;
    private String description;
    private String status;
}
