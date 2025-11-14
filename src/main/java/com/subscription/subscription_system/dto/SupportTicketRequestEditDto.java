package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportTicketRequestEditDto {
    private String subject;
    private String issueDescription;
    private String status;
    private String ticketStatus;
}
