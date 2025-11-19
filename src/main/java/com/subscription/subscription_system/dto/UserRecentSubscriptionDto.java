package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecentSubscriptionDto {
    private String planName;
    private double price;
    private String planType;
    private String paymentStatus; // Paid / Due
}
