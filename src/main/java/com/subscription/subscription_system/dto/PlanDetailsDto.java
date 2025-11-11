package com.subscription.subscription_system.dto;

import lombok.Data;

@Data
public class PlanDetailsDto {
    private String id;
    private String planName;
    private String planType;
    private String planCost;
    private String description;
    private String status;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
}
