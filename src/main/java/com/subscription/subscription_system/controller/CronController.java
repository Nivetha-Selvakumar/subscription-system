package com.subscription.subscription_system.controller;

import com.subscription.subscription_system.dto.CommonResponseDto;
import com.subscription.subscription_system.dto.ExpiredResultDto;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("api/")
public class CronController {

    @Autowired
    SubscriptionService subscriptionService;

    @Value("${cron.subscription.expiry}")
    private String expiryCron;

    // Cron ON/OFF switch
    @Value("${cron.subscription.run}")
    private boolean isCronEnabled;

    @Value("${cron.subscription.reminder}")
    private String reminderCron;

    @Value("${cron.subscription.reminder.run}")
    private boolean isCronReminder;

    @Scheduled(cron = "${cron.subscription.reminder}")
    public void autoSendExpiryReminder() {

        log.info("🔔 Running subscription reminder cron (3 days before expiry)...");

        if (!isCronReminder) {
            log.info("Cron is disabled. Skipping subscription reminder job.");
            return;
        }

        ExpiredResultDt. o result = subscriptionService.notifyUsersBeforeExpiry();

        log.info("Reminder cron completed: {}", result);
    }


    @Scheduled(cron = "${cron.subscription.expiry}")
    public void autoExpireSubscriptions() throws CommonException {

        // If disabled — do NOT run cron
        if (!isCronEnabled) {
            log.info("Cron is disabled. Skipping subscription expiry job.");
            return;
        }

        log.info("🔄 Running subscription expiry cron...");

        ExpiredResultDto result = subscriptionService.expireSubscriptions();

        log.info("✔ Cron Completed. Result: {}", result);
    }

    // Run manually OR via Cron
    @GetMapping("/plan/expires")
    public ResponseEntity<CommonResponseDto> expirePlansCron() throws CommonException {
        log.info("Checking expired subscription plans...");

        ExpiredResultDto expiredCount = subscriptionService.expireSubscriptions();

        CommonResponseDto response =
                new CommonResponseDto("Expired plans updated", HttpStatus.OK.value(), expiredCount);

        return ResponseEntity.ok(response);
    }




}
