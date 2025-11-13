package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDetailsDto {
    private String id;
    private Object user;
    private String ratings;
    private String comments;
    private String status;
    private String createdAt;
    private String createdBy;
    private String updatedAt;
    private String updatedBy;
}
