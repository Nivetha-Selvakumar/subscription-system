package com.subscription.subscription_system.entity;

import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.enumuration.EnumTicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "Support_Ticket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketEntity {

    @Id
    private String id;

    @DBRef
    @Field("user_id")
    private UserEntity user;

    @Field("issue_desc")
    private String issueDescription;

    @Field("status")
    private EnumStatusType status;

    @Field("ticket_status")
    private EnumTicketStatus ticketStatus;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("created_by")
    private String createdBy;

    @Field("updated_by")
    private String updatedBy;
}
