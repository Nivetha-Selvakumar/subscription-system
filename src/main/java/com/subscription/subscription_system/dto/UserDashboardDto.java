package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDashboardDto {
    private String activePlan;
    private String nextBillingDate;
    private double totalSpent;
    private List<UserRecentSubscriptionDto> recentSubscriptions;
    private UserUpcomingPaymentDto upcomingPayment;

}
