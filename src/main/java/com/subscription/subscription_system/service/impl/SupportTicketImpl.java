package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.SupportResponseEntity;
import com.subscription.subscription_system.entity.SupportTicketEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumTicketStatus;
import com.subscription.subscription_system.enumuration.EnumUserType;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.SupportTicketMapper;
import com.subscription.subscription_system.repository.AdminRepo;
import com.subscription.subscription_system.repository.SupportResponseRepo;
import com.subscription.subscription_system.repository.SupportTicketRepo;
import com.subscription.subscription_system.service.SupportTicketService;
import com.subscription.subscription_system.utils.QueryUtils;
import com.subscription.subscription_system.validation.BusinessValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired
    MongoTemplate mongoTemplate;

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
            ticket.setUpdatedAt(LocalDateTime.now());
            supportTicketRepo.save(ticket);
        }

        return supportResponse;
    }

    @Override
    public SupportTicketDetailsDto getSupportTicketDetails(String userId, String targetTicketId) throws CommonException {

        log.info("Get Ticket Details API invoked by User: {}", userId);

        // Validate User
        UserEntity user = businessValidation.validateUserOrNot(userId);

        // Ticket exists
        SupportTicketEntity ticket = supportTicketRepo
                .findByIdAndStatusNot(targetTicketId, EnumStatusType.DELETE)
                .orElseThrow(() -> new CommonException("Ticket not found", HttpStatus.BAD_REQUEST.value()));

        // Check: User can only view their own tickets
        if (!ticket.getUser().getId().equals(user.getId()) && !user.getRole().equals(EnumUserType.ADMIN)) {
            throw new CommonException("You cannot access another user's ticket", HttpStatus.FORBIDDEN.value());
        }

        // Fetch responses
        List<SupportResponseEntity> responses =
                supportResponseRepo.findByTicketAndStatusOrderByResponseNoAsc(
                        ticket,
                        EnumStatusType.ACTIVE
                );

        // Map to DTO
        return supportTicketMapper.mapToSupportTicketDetails(ticket, responses);
    }


    @Override
    public CommonPaginatedResponse<SupportTicketDetailsDto> getTicketList(
            String userId,
            String search,
            String filterBy,
            String sortBy,
            String sortDir,
            int offset,
            int limit) throws CommonException {

        log.info("Get Support Ticket List API invoked by {}", userId);

        // 1️⃣ Validate user
        UserEntity user = businessValidation.validateUserOrNot(userId);

        // 2️⃣ Check if admin or normal user
        boolean isAdmin = adminRepo.findByUserAndStatus(user, EnumStatusType.ACTIVE).isPresent();

        // 3️⃣ Build the query based on role
        Query query = QueryUtils.buildSupportTicketQuery(
                isAdmin ? null : user.getId(),   // If admin → no user filter
                search,
                filterBy,
                sortBy,
                sortDir
        );

        // 4️⃣ Total count
        long totalCount = mongoTemplate.count(query, SupportTicketEntity.class);

        // 5️⃣ Apply pagination
        query.skip(offset).limit(limit);

        // 6️⃣ Fetch results
        List<SupportTicketEntity> ticketList = mongoTemplate.find(query, SupportTicketEntity.class);

        // 7️⃣ Map results
        List<SupportTicketDetailsDto> dtoList = ticketList.stream().map(ticket -> {
            List<SupportResponseEntity> responses =
                    supportResponseRepo.findByTicketAndStatusOrderByResponseNoAsc(
                            ticket, EnumStatusType.ACTIVE
                    );
            return supportTicketMapper.mapToSupportTicketDetails(ticket, responses);
        }).toList();

        // 8️⃣ Return paginated response
        return new CommonPaginatedResponse<>(dtoList, totalCount);
    }

    @Override
    public SupportTicketDetailsDto editSupportTicket(
            String userId,
            String targetTicketId,
            SupportTicketRequestEditDto dto
    ) throws CommonException {

        if (dto.getIssueDescription() == null || dto.getIssueDescription().isBlank()) {
            throw new CommonException("Issue Description is required", HttpStatus.BAD_REQUEST.value());
        }

        UserEntity user = businessValidation.validateUserOrNot(userId);

        SupportTicketEntity ticket = supportTicketRepo
                .findByIdAndStatusNot(targetTicketId, EnumStatusType.DELETE)
                .orElseThrow(() -> new CommonException("Ticket Not Found", 400));

        // Only owner can edit
        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new CommonException("Only ticket owner can edit this ticket", 403);
        }

        // Cannot edit closed ticket
        if (ticket.getTicketStatus().equals(EnumTicketStatus.CLOSED)) {
            throw new CommonException("Cannot edit a closed ticket", 400);
        }

        ticket.setIssueDescription(dto.getIssueDescription());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setUpdatedBy(user.getFirstName() + " " + user.getLastName());

        supportTicketRepo.save(ticket);

        // Responses for mapping
        List<SupportResponseEntity> responses =
                supportResponseRepo.findByTicketAndStatusOrderByResponseNoAsc(
                        ticket, EnumStatusType.ACTIVE);

        return supportTicketMapper.mapToSupportTicketDetails(ticket, responses);
    }

    @Override
    public SupportResponseEntity editSupportTicketResponse(
            String userId,
            String responseId,
            SupportTicketResponseEditDto dto
    ) throws CommonException {

        if (dto.getResponseText() == null || dto.getResponseText().isBlank()) {
            throw new CommonException("Response Text is required", 400);
        }

        UserEntity user = businessValidation.validateUserOrNot(userId);

        SupportResponseEntity response = supportResponseRepo.findById(responseId)
                .orElseThrow(() -> new CommonException("Response not found", 400));

        // Cannot edit deleted response
        if (response.getStatus().equals(EnumStatusType.DELETE)) {
            throw new CommonException("Cannot edit a deleted response", 400);
        }

        // Cannot edit if ticket closed
        if (response.getTicket().getTicketStatus().equals(EnumTicketStatus.CLOSED)) {
            throw new CommonException("Cannot edit response of closed ticket", 400);
        }

        // Check role: responder OR admin
        boolean isAdmin = adminRepo.findByUserAndStatus(user, EnumStatusType.ACTIVE).isPresent();
        boolean isOwner = response.getResponder().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new CommonException("Only responder or admin can edit response", 403);
        }

        response.setResponseText(dto.getResponseText());
        response.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
        response.setUpdatedAt(LocalDateTime.now());

        return supportResponseRepo.save(response);
    }

    @Override
    public void deleteSupportTicketResponse(String userId, String responseId) throws CommonException {

        UserEntity user = businessValidation.validateUserOrNot(userId);

        SupportResponseEntity response = supportResponseRepo.findById(responseId)
                .orElseThrow(() -> new CommonException("Response not found", 400));

        boolean isAdmin = adminRepo.findByUserAndStatus(user, EnumStatusType.ACTIVE).isPresent();
        boolean isOwner = response.getResponder().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new CommonException("Only responder or admin can delete", 403);
        }

        response.setStatus(EnumStatusType.DELETE);
        response.setUpdatedAt(LocalDateTime.now());
        response.setUpdatedBy(user.getFirstName() + " " + user.getLastName());

        supportResponseRepo.save(response);
    }




}
