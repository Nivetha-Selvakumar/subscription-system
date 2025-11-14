package com.subscription.subscription_system.mapper;

import com.subscription.subscription_system.dto.SupportTicketDetailsDto;
import com.subscription.subscription_system.dto.SupportTicketResponseCreateDto;
import com.subscription.subscription_system.dto.SupportTicketResponseDetailsDto;
import com.subscription.subscription_system.entity.SupportResponseEntity;
import com.subscription.subscription_system.entity.SupportTicketEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumTicketStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SupportTicketMapper {


    public SupportTicketEntity mapToSupportTicket(UserEntity user, String issueDescription, String subject) {
        SupportTicketEntity supportTicketEntity = new SupportTicketEntity();
        supportTicketEntity.setUser(user);
        supportTicketEntity.setSubject(subject);
        supportTicketEntity.setIssueDescription(issueDescription);
        supportTicketEntity.setTicketStatus(EnumTicketStatus.OPEN);
        supportTicketEntity.setStatus(EnumStatusType.ACTIVE);
        supportTicketEntity.setCreatedAt(LocalDateTime.now());
        supportTicketEntity.setUpdatedAt(LocalDateTime.now());
        supportTicketEntity.setCreatedBy(user.getFirstName() + " " + user.getLastName());
        supportTicketEntity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());
        return supportTicketEntity;
    }

    public SupportResponseEntity mapToSupportTicketResponse(
            UserEntity responder,
            SupportTicketResponseCreateDto dto,
            SupportTicketEntity ticket
    ) {
        SupportResponseEntity response = new SupportResponseEntity();

        response.setTicket(ticket);
        response.setResponder(responder);
        response.setResponseText(dto.getResponseText());

        return response;
    }

    public SupportTicketDetailsDto mapToSupportTicketDetails(
            SupportTicketEntity ticket,
            List<SupportResponseEntity> responses
    ) {
        SupportTicketDetailsDto dto = new SupportTicketDetailsDto();

        dto.setId(ticket.getId());
        dto.setUser(ticket.getUser());
        dto.setIssueDescription(ticket.getIssueDescription());
        dto.setSubject(ticket.getSubject());
        dto.setStatus(ticket.getStatus().name());
        dto.setTicketStatus(ticket.getTicketStatus().name());
        dto.setCreatedAt(ticket.getCreatedAt().toString());
        dto.setUpdateAt(ticket.getUpdatedBy() != null ? ticket.getUpdatedAt().toString() : null);
        dto.setCreatedBy(ticket.getCreatedBy());
        dto.setUpdatedBy(ticket.getUpdatedBy());

        List<Object> responseList = responses.stream()
                .map(res -> {
                    SupportTicketResponseDetailsDto r = new SupportTicketResponseDetailsDto();
                    r.setId(res.getId());
                    r.setTicket(null);  // avoid recursion
                    r.setResponseNo(String.valueOf(res.getResponseNo()));
                    r.setResponseText(res.getResponseText());
                    r.setRespondedAt(res.getRespondedAt().toString());
                    r.setResponder(res.getResponder());
                    r.setStatus(res.getStatus().name());
                    r.setCreatedAt(res.getCreatedAt().toString());
                    r.setUpdatedAt(res.getUpdatedAt().toString());
                    r.setCreatedBy(res.getCreatedBy());
                    r.setUpdatedBy(res.getUpdatedBy());
                    return r;
                })
                .collect(Collectors.toList());

        dto.setTicketResponse(responseList);

        return dto;
    }

}
