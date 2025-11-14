package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.SupportResponseEntity;
import com.subscription.subscription_system.entity.SupportTicketEntity;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

@Component
public interface SupportTicketService {
    SupportTicketEntity createSupportTicket(String userId, SupportTicketRequestCreateDto supportTicketRequestDto) throws CommonException;

    SupportResponseEntity createSupportTicketResponse(String userId, String targetTicketId,SupportTicketResponseCreateDto supportTicketRequestDto) throws CommonException;

    SupportTicketDetailsDto getSupportTicketDetails(String userId, String targetTicketId) throws CommonException;

    CommonPaginatedResponse<SupportTicketDetailsDto> getTicketList(String userId, String search, String filterBy, String sortBy, String sortDir, int offset, int limit) throws CommonException;

    SupportTicketDetailsDto editSupportTicket(String userId, String targetTicketId, SupportTicketRequestEditDto dto) throws CommonException;

    SupportResponseEntity editSupportTicketResponse(String userId, String responseId, SupportTicketResponseEditDto dto) throws CommonException;

    void deleteSupportTicketResponse(String userId, String responseId) throws CommonException;
}
