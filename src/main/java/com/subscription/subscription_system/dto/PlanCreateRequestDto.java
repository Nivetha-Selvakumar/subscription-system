package com.subscription.subscription_system.dto;

import lombok.Data;

@Data
public class PlanCreateRequestDto {
    private String planName;
    private String planType;
    private String planCost;
    private String description;
}
