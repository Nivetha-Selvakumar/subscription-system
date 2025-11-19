package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardRequestDto {
    private long totalUsers;
    private long activeSubscriptions;
    private double monthlyRevenue;
    private long pendingTickets;

    private SubscriptionChartDto statistics;
    private List<ActivityDto> recentActivities;


}
