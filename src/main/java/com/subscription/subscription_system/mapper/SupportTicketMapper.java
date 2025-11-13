package com.subscription.subscription_system.mapper;

import com.subscription.subscription_system.dto.SupportTicketResponseCreateDto;
import com.subscription.subscription_system.entity.SupportResponseEntity;
import com.subscription.subscription_system.entity.SupportTicketEntity;
import com.subscription.subscription_system.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class SupportTicketMapper {


    public SupportTicketEntity mapToSupportTicket(UserEntity user, String issueDescription) {
        return null;
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

}
