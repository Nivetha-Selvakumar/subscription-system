package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.*;
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
        supportTicketValidator.validateSupportTicketResponseCreate(userId, targetTicketId, supportTicketRequestDto);

        SupportResponseEntity supportResponse = supportTicketService.createSupportTicketResponse(userId, targetTicketId, supportTicketRequestDto);

        CommonResponseDto response = new CommonResponseDto("Support Ticket Response Created Successfully", HttpStatus.CREATED.value(), supportResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/supportTicketDetails")
    public ResponseEntity<CommonResponseDto> getSupportTicketDetails(@RequestHeader("User-Id") String userId,
                                                                     @RequestParam("targetTicketId") String targetTicketId) throws CommonException {

        log.info("Basic validation for Get the Ticket Details");

        if (userId == null || targetTicketId == null) {
            throw new CommonException("UserId or Target Ticket Id is invalid", HttpStatus.BAD_REQUEST.value());
        }

        SupportTicketDetailsDto supportTicketDetailsDto = supportTicketService.getSupportTicketDetails(userId, targetTicketId);

        CommonResponseDto response = new CommonResponseDto("Support Ticket Details Fetched Successfully", HttpStatus.OK.value(), supportTicketDetailsDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/supportTicketList")
    public ResponseEntity<CommonResponseDto> getSupportTicketList(@RequestHeader("User-Id") String userId,
                                                                  @RequestParam(required = false) String search,
                                                                  @RequestParam(required = false) String filterBy,  // format: key:value,key:value
                                                                  @RequestParam(required = false, defaultValue = "firstName") String sortBy,
                                                                  @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                                  @RequestParam(required = false, defaultValue = "0") int offset,

                                                                  @RequestParam(required = false, defaultValue = "10") int limit) throws CommonException {

        log.info("Basic validation for Support Ticket List");
        if (userId == null) {
            throw new CommonException("UserId is invalid", HttpStatus.BAD_REQUEST.value());
        }

        log.info("Fetching Support Ticket details list");
        CommonPaginatedResponse<SupportTicketDetailsDto> supportTicketServiceTicketList = supportTicketService.getTicketList(userId, search, filterBy, sortBy, sortDir, offset, limit);

        CommonResponseDto response = new CommonResponseDto("Support Ticket Details Fetched Successfully", HttpStatus.OK.value(), supportTicketServiceTicketList);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit/supportTicket")
    public ResponseEntity<CommonResponseDto> editSupportTicket(
            @RequestHeader("User-Id") String userId,
            @RequestParam("targetTicketId") String targetTicketId,
            @RequestBody SupportTicketRequestEditDto dto  // issueDescription only
    ) throws CommonException {

        log.info("Validation for Edit Support Ticket");
        supportTicketValidator.validateSupportTicketEdit(userId, targetTicketId, dto);
        SupportTicketDetailsDto updated =
                supportTicketService.editSupportTicket(userId, targetTicketId, dto);

        CommonResponseDto response = new CommonResponseDto(
                "Support Ticket Updated Successfully",
                HttpStatus.OK.value(),
                updated
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit/supportTicketResponse")
    public ResponseEntity<CommonResponseDto> editSupportTicketResponse(
            @RequestHeader("User-Id") String userId,
            @RequestParam("responseId") String responseId,
            @RequestBody SupportTicketResponseEditDto dto
    ) throws CommonException {

        log.info("Editing Support Ticket Response");
        supportTicketValidator.validateSupportResponseEdit(userId, responseId, dto);

        SupportResponseEntity updatedResponse =
                supportTicketService.editSupportTicketResponse(userId, responseId, dto);

        CommonResponseDto response = new CommonResponseDto(
                "Support Ticket Response Updated Successfully",
                HttpStatus.OK.value(),
                updatedResponse
        );

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/supportTicketResponse")
    public ResponseEntity<CommonResponseDto> deleteSupportTicketResponse(
            @RequestHeader("User-Id") String userId,
            @RequestParam("responseId") String responseId
    ) throws CommonException {
        if (userId == null || responseId == null) {
            throw new CommonException("UserId or Response Id is invalid", HttpStatus.BAD_REQUEST.value());
        }

        supportTicketService.deleteSupportTicketResponse(userId, responseId);

        CommonResponseDto response =
                new CommonResponseDto("Support Ticket Response Deleted Successfully",
                        HttpStatus.OK.value(), null);

        return ResponseEntity.ok(response);
    }
}
