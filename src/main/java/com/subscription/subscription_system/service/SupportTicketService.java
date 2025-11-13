package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.SupportTicketRequestCreateDto;
import com.subscription.subscription_system.dto.SupportTicketResponseCreateDto;
import com.subscription.subscription_system.entity.SupportResponseEntity;
import com.subscription.subscription_system.entity.SupportTicketEntity;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

@Component
public interface SupportTicketService {
    SupportTicketEntity createSupportTicket(String userId, SupportTicketRequestCreateDto supportTicketRequestDto) throws CommonException;

    SupportResponseEntity createSupportTicketResponse(String userId, String targetTicketId,SupportTicketResponseCreateDto supportTicketRequestDto) throws CommonException;
}
