package com.subscription.subscription_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    private String status;

    @Field("created_at")
    private String createdAt;

    @Field("updated_at")
    private String updateAt;
}
