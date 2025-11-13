package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.SupportTicketRequestCreateDto;
import com.subscription.subscription_system.dto.SupportTicketResponseCreateDto;
import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.SupportResponseEntity;
import com.subscription.subscription_system.entity.SupportTicketEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumTicketStatus;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.SupportTicketMapper;
import com.subscription.subscription_system.repository.AdminRepo;
import com.subscription.subscription_system.repository.SupportResponseRepo;
import com.subscription.subscription_system.repository.SupportTicketRepo;
import com.subscription.subscription_system.service.SupportTicketService;
import com.subscription.subscription_system.validation.BusinessValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class SupportTicketImpl implements SupportTicketService {

    @Autowired
    BusinessValidation businessValidation;

    @Autowired
    SupportTicketRepo supportTicketRepo;

    @Autowired
    AdminRepo adminRepo;

    @Autowired
    SupportTicketMapper supportTicketMapper;

    @Autowired
    SupportResponseRepo supportResponseRepo;

    @Override
    public SupportTicketEntity createSupportTicket(String userId, SupportTicketRequestCreateDto supportTicketRequestDto) throws CommonException {

        if (supportTicketRequestDto.getIssueDescription() == null) {
            throw new CommonException("Issue Description is Required", HttpStatus.BAD_REQUEST.value());
        }

        log.info("Create Support Ticket API invoked by User Id: {}", userId);
        UserEntity user = businessValidation.validateUserOrNot(userId);
        Optional<AdminEntity> existingAdmin = adminRepo.findByUserAndStatus(user, EnumStatusType.ACTIVE);

        if (existingAdmin.isPresent()) {
            throw new CommonException("Admin Cannot Raise a Ticket", HttpStatus.BAD_REQUEST.value());
        }

        SupportTicketEntity supportTicketEntity = supportTicketMapper.mapToSupportTicket(user, supportTicketRequestDto.getIssueDescription());
        supportTicketRepo.save(supportTicketEntity);
        return supportTicketEntity;
    }

    @Override
    public SupportResponseEntity createSupportTicketResponse(
            String userId,
            String targetTicketId,
            SupportTicketResponseCreateDto dto
    ) throws CommonException {

        if (dto.getResponseText() == null || dto.getResponseText().isBlank()) {
            throw new CommonException("Response Text is Required", HttpStatus.BAD_REQUEST.value());
        }

        log.info("Create Support Ticket Response API invoked by User Id: {}", userId);

        // Validate user
        UserEntity responder = businessValidation.validateUserOrNot(userId);

        // Get ticket
        SupportTicketEntity ticket = supportTicketRepo
                .findByIdAndStatusNot(targetTicketId, EnumStatusType.DELETE)
                .orElseThrow(() -> new CommonException("Ticket Not Found", HttpStatus.BAD_REQUEST.value()));

        // Block response on CLOSED ticket
        if (ticket.getTicketStatus().equals(EnumTicketStatus.CLOSED)) {
            throw new CommonException("Cannot respond to a closed ticket", HttpStatus.BAD_REQUEST.value());
        }

        // Auto-increment responseNo
        int nextResponseNo = supportResponseRepo.countByTicket(ticket) + 1;

        // Mapping
        SupportResponseEntity supportResponse = supportTicketMapper
                .mapToSupportTicketResponse(responder, dto, ticket);

        supportResponse.setResponseNo(nextResponseNo);
        supportResponse.setCreatedAt(LocalDateTime.now());
        supportResponse.setUpdatedAt(LocalDateTime.now());
        supportResponse.setRespondedAt(LocalDateTime.now());
        supportResponse.setCreatedBy(responder.getFirstName() + " " + responder.getLastName());
        supportResponse.setUpdatedBy(responder.getFirstName() + " " + responder.getLastName());
        supportResponse.setStatus(EnumStatusType.ACTIVE);

        // Save response
        supportResponseRepo.save(supportResponse);

        // Update ticket status
        if (ticket.getTicketStatus().equals(EnumTicketStatus.OPEN)) {
            ticket.setTicketStatus(EnumTicketStatus.IN_PROGRESS);
            ticket.setUpdateAt(LocalDateTime.now());
            supportTicketRepo.save(ticket);
        }

        return supportResponse;
    }


}
