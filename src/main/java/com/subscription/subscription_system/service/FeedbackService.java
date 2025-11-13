package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.FeedbackEntity;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

@Component
public interface FeedbackService {
    FeedbackEntity createFeedback(String userId, FeedbackCreateRequestDto feedbackCreateRequest) throws CommonException;

    FeedbackDetailsDto getFeedbackDetails(String userId, String feedbackId) throws CommonException;

    CommonPaginatedResponse<FeedbackDetailsDto> getFeedbackList(String userId, String search, String filterBy, String sortBy, String sortDir, int offset, int limit) throws CommonException;

    FeedbackDetailsDto editFeedback(FeedbackEditRequestDto feedbackEditRequestDto, String userId, String targetFeedbackId) throws CommonException;

    void deleteFeedback(String userId, String targetFeedbackId) throws CommonException;
}
