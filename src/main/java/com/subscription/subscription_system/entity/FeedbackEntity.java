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

import java.time.LocalDateTime;

@Document(collection = "Feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackEntity {

    @Id
    private String id; // feedback_id

    @DBRef
    @Field("user_id")
    private UserEntity user;  // FK → User(user_id)

    @Field("ratings")
    private Integer ratings; // 1–5

    @Field("comments")
    private String comments; // feedback or suggestions

    @Field("status")
    private EnumStatusType status;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("created_by")
    private String createdBy;

    @Field("updated_by")
    private String updatedBy;
}
