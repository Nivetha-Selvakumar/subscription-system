package com.subscription.subscription_system.mapper;

import com.subscription.subscription_system.dto.FeedbackDetailsDto;
import com.subscription.subscription_system.entity.FeedbackEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FeedbackMapper
{
    public FeedbackEntity mapToFeedbackEntity(UserEntity user, String ratings, String comments, EnumStatusType status, String createdBy) {
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setUser(user);
        feedback.setRatings(Integer.valueOf(ratings));
        feedback.setComments(comments);
        feedback.setStatus(status);
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setUpdatedAt(LocalDateTime.now());
        feedback.setCreatedBy(createdBy);
        feedback.setUpdatedBy(createdBy);
        return feedback;
    }

    public FeedbackDetailsDto mapToFeedbackDetailsDto(FeedbackEntity entity) {
        FeedbackDetailsDto dto = new FeedbackDetailsDto();
        dto.setId(entity.getId());
        dto.setUser(entity.getUser());
        dto.setRatings(String.valueOf(entity.getRatings()));
        dto.setComments(entity.getComments());
        dto.setStatus(entity.getStatus().name());
        dto.setCreatedAt(String.valueOf(entity.getCreatedAt()));
        dto.setUpdatedAt(String.valueOf(entity.getUpdatedAt()));
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        return dto;
    }

    public void mapEditToFeedbackEntity(String ratings, String comments, EnumStatusType status, FeedbackEntity entity, String user) {
        if (ratings != null) entity.setRatings(Integer.valueOf(ratings));
        if (comments != null) entity.setComments(comments);
        if (status != null) entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(user);
    }
}

