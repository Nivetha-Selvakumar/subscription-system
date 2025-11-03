package com.subscription.subscription_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

    @Id
    private String id;

    @DBRef
    @Field("user_id")
    private UserEntity user;

    @DBRef
    @Field("plan_id")
    private SubscriptionPlanEntity plan;

    @Field("amount")
    private Double amount;

    @Field("payment_dt")
    private String paymentDate;

    @Field("payment_status")
    private String paymentStatus;
}
