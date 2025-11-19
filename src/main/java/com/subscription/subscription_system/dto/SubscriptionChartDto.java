package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionChartDto {
    private List<String> labels;
    private List<Integer> values;
}
