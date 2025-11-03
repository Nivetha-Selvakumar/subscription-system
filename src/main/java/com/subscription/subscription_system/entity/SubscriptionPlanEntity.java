package com.subscription.subscription_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Subscription_Plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanEntity {

    @Id
    private String id;

    @Field("plan_name")
    private String planName;

    @Field("plan_type")
    private String planType;

    @Field("cost")
    private Double cost;
}
