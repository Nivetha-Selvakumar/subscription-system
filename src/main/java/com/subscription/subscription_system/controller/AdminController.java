package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.AdminCreateRequestDto;
import com.subscription.subscription_system.dto.AdminCreateResponseDto;
import com.subscription.subscription_system.dto.AdminDashboardRequestDto;
import com.subscription.subscription_system.dto.CommonResponseDto;
import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.service.AdminService;
import com.subscription.subscription_system.validator.basicValidation.AdminValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    AdminValidator adminValidator;

    @PostMapping("/create/admin")
    public ResponseEntity<AdminCreateResponseDto> createAdmin(@RequestBody AdminCreateRequestDto adminCreateDto) throws CommonException {
        //Basic Validation for creating Admin
        log.info("Basic Validation for creating a Admin");
        adminValidator.validateAdminCreate(adminCreateDto);
        //Creating Admin
        log.info("Creating a Admin");
        AdminEntity admin = adminService.createAdmin(adminCreateDto);

        AdminCreateResponseDto response = new AdminCreateResponseDto( "Admin created successfully", HttpStatus.CREATED.value(), admin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/dashboard")
    public ResponseEntity<CommonResponseDto> getAdminDashboard(@RequestHeader("User-Id") String userId) throws CommonException {
        log.info("Admin Dashboard api called");
        AdminDashboardRequestDto adminDashboardRequestDto = adminService.getAdminDashboard(userId);
        CommonResponseDto response = new CommonResponseDto("Admin Dashboard List fetched successfully", HttpStatus.OK.value(), adminDashboardRequestDto);
        return ResponseEntity.ok(response);

    }

}
