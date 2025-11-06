package com.subscription.subscription_system.entity;

import com.subscription.subscription_system.enumuration.EnumStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Support_Response")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupportResponseEntity {

    @Id
    private String id;

    @DBRef
    @Field("ticket_id")
    private SupportTicketEntity ticket;

    @Field("response_no")
    private Integer responseNo;

    @Field("response_txt")
    private String responseText;

    @Field("responded_at")
    private String respondedAt;

    @Field("responder")
    private String responder;

    @Field("status")
    private EnumStatusType status;
}
