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

@Document(collection = "Subscriber")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberEntity {

    @Id
    private String id;

    @DBRef(lazy = true)
    @Field("user_id")
    private UserEntity user;

    @Field("current_sub_status")
    private String currentSubStatus;

    @Field("sub_start_dt")
    private String subStartDate;

    @Field("sub_end_dt")
    private String subEndDate;

    @Field("join_dt")
    private String joinDate;

    @Field("status")
    private EnumStatusType status;
}
