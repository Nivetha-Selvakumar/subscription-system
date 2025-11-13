package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.CommonResponseDto;
import com.subscription.subscription_system.dto.SupportTicketRequestCreateDto;
import com.subscription.subscription_system.dto.SupportTicketResponseCreateDto;
import com.subscription.subscription_system.entity.SupportResponseEntity;
import com.subscription.subscription_system.entity.SupportTicketEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.service.SupportTicketService;
import com.subscription.subscription_system.validator.basicValidation.SupportTicketValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/")
public class SupportTicketController {

    @Autowired
    SupportTicketService supportTicketService;

    @Autowired
    SupportTicketValidator supportTicketValidator;


    @PostMapping("/create/supportTicket")
    public ResponseEntity<CommonResponseDto> createSupport(
            @RequestHeader("User-Id") String userId,
            @RequestBody SupportTicketRequestCreateDto supportTicketRequestDto) throws CommonException {
        log.info("Basic Validation for Support Ticket Create");
        if (userId == null) {
            throw new CommonException("UserId is invalid", HttpStatus.BAD_REQUEST.value());
        }
        SupportTicketEntity supportTicketEntity = supportTicketService.createSupportTicket(userId, supportTicketRequestDto);
        CommonResponseDto response = new CommonResponseDto("Support Ticket Created Successfully", HttpStatus.CREATED.value(), supportTicketEntity);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create/supportTicketResponse")
    public ResponseEntity<CommonResponseDto> createSupportTicketResponse(
            @RequestHeader("User-Id") String userId,
            @RequestParam("targetTicketId") String targetTicketId,
            @RequestBody SupportTicketResponseCreateDto supportTicketRequestDto) throws CommonException {

        log.info("Basic Validation for Support Ticket Response Create");
        supportTicketValidator.validateSupportTicketResponseCreate(userId,targetTicketId,supportTicketRequestDto);

        SupportResponseEntity supportResponse = supportTicketService.createSupportTicketResponse(userId, targetTicketId, supportTicketRequestDto);

        CommonResponseDto response = new CommonResponseDto("Support Ticket Response Created Successfully", HttpStatus.CREATED.value(), supportResponse);

        return ResponseEntity.ok(response);
    }


}
