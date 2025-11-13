package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.*;
import com.subscription.subscription_system.entity.FeedbackEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.FeedbackMapper;
import com.subscription.subscription_system.repository.FeedbackRepo;
import com.subscription.subscription_system.service.FeedbackService;
import com.subscription.subscription_system.utils.QueryUtils;
import com.subscription.subscription_system.validation.BusinessValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FeedbackImpl implements FeedbackService {

    @Autowired
    BusinessValidation businessValidation;

    @Autowired
    FeedbackRepo feedbackRepo;

    @Autowired
    FeedbackMapper feedbackMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public FeedbackEntity createFeedback(String userId, FeedbackCreateRequestDto feedbackCreateRequest)
            throws CommonException {

        // User Of Admin Exist or not
        log.info("Create Plan API invoked by Feedback: {}", userId);
        UserEntity user = businessValidation.validateUserOrNot(userId);


        // 3Map DTO → Entity
        FeedbackEntity feedbackEntity = feedbackMapper.mapToFeedbackEntity(
                user,
                feedbackCreateRequest.getRatings(),
                feedbackCreateRequest.getComments(),
                EnumStatusType.ACTIVE,
                user.getFirstName() + " " + user.getLastName()
        );

        feedbackRepo.save(feedbackEntity);
        log.info("✅ Feedback created successfully by user: {}", user.getEmail());

        return feedbackEntity;
    }

    @Override
    public FeedbackDetailsDto getFeedbackDetails(String userId, String feedbackId) throws CommonException {
        log.info("Fetching feedback details for feedbackId: {} by userId: {}", feedbackId, userId);
        businessValidation.validateUserOrNot(userId);

        FeedbackEntity feedbackEntity = businessValidation.feedbackExist(feedbackId);

        return feedbackMapper.mapToFeedbackDetailsDto(feedbackEntity);
    }

    @Override
    public CommonPaginatedResponse<FeedbackDetailsDto> getFeedbackList(String userId, String search, String filterBy, String sortBy, String sortDir, int offset, int limit) throws CommonException {
        log.info("📋 Fetching feedback list for userId: {}", userId);

        businessValidation.validateUserOrNot(userId);

        Query query = QueryUtils.buildFeedbackQuery(search, filterBy, sortBy, sortDir);

        long totalCount = mongoTemplate.count(query, FeedbackEntity.class);
        query.skip(offset).limit(limit);

        List<FeedbackEntity> feedbackList = mongoTemplate.find(query, FeedbackEntity.class);

        List<FeedbackDetailsDto> feedbackDetailsDtoList = feedbackList.stream()
                .map(feedbackMapper::mapToFeedbackDetailsDto)
                .collect(Collectors.toList());

        log.info("✅ Fetched {} feedback records (totalCount={})", feedbackDetailsDtoList.size(), totalCount);

        return new CommonPaginatedResponse<>(feedbackDetailsDtoList, totalCount);

    }

    @Override
    public FeedbackDetailsDto editFeedback(FeedbackEditRequestDto feedbackEditRequestDto, String userId, String targetFeedbackId) throws CommonException {
        log.info("✏️ Editing feedback with ID: {}", targetFeedbackId);
        UserEntity user = businessValidation.validateUserOrNot(userId);

        FeedbackEntity feedbackEntity = businessValidation.feedbackExist(targetFeedbackId);

        if (!Objects.equals(user.getId(), feedbackEntity.getUser().getId())) {
            throw new CommonException("Feedback Edit of other user is not possible", HttpStatus.BAD_REQUEST.value());
        }

        // Update fields
        feedbackMapper.mapEditToFeedbackEntity(
                feedbackEditRequestDto.getRatings(),
                feedbackEditRequestDto.getComments(),
                EnumStatusType.fromValue(feedbackEditRequestDto.getStatus()),
                feedbackEntity, user.getFirstName() + " " + user.getLastName()
        );

        feedbackRepo.save(feedbackEntity);
        log.info("✅ Feedback updated successfully for ID: {}", feedbackEntity.getId());

        return feedbackMapper.mapToFeedbackDetailsDto(feedbackEntity);
    }

    @Override
    public void deleteFeedback(String userId, String targetFeedbackId) throws CommonException {

        log.info("🗑️ Deleting feedback ID: {} by userId: {}", targetFeedbackId, userId);

        UserEntity user = businessValidation.validateUserOrNot(userId);

        FeedbackEntity feedbackEntity = businessValidation.feedbackExist(targetFeedbackId);

        if (!Objects.equals(user.getId(), feedbackEntity.getUser().getId())) {
            throw new CommonException("Feedback Delete of other user is not possible", HttpStatus.BAD_REQUEST.value());
        }

        feedbackEntity.setStatus(EnumStatusType.DELETE);
        feedbackEntity.setUpdatedAt(LocalDateTime.now());
        feedbackEntity.setUpdatedBy(user.getFirstName() + " " + user.getLastName());

        feedbackRepo.save(feedbackEntity);
        log.info("✅ Feedback soft-deleted successfully (ID: {})", targetFeedbackId);
    }
}
