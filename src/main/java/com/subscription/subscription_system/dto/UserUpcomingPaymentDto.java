package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpcomingPaymentDto {
    private double amount;
    private String dueDate;
}
