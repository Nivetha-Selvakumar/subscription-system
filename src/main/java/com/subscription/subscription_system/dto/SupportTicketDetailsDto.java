package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportTicketDetailsDto {

    private String id;
    private Object user;
    private String subject;
    private String issueDescription;
    private String status;
    private String ticketStatus;
    private String createdAt;
    private String updateAt;
    private String createdBy;
    private String updatedBy;
    private List<Object> ticketResponse;





}
