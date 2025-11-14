package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketResponseDetailsDto {
    private String id;
    private Object ticket;
    private String responseNo;
    private String responseText;
    private String respondedAt;
    private Object responder;
    private String status;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;

}


