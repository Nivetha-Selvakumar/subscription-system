package com.subscription.subscription_system.service.impl;

import com.subscription.subscription_system.dto.ActivityDto;
import com.subscription.subscription_system.dto.AdminCreateRequestDto;
import com.subscription.subscription_system.dto.AdminDashboardRequestDto;
import com.subscription.subscription_system.dto.SubscriptionChartDto;
import com.subscription.subscription_system.entity.*;
import com.subscription.subscription_system.enumuration.*;
import com.subscription.subscription_system.exception.CommonException;
import com.subscription.subscription_system.mapper.AdminMapper;
import com.subscription.subscription_system.mapper.UserMapper;
import com.subscription.subscription_system.repository.*;
import com.subscription.subscription_system.service.AdminService;
import com.subscription.subscription_system.validation.BusinessValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AdminServiceImpl implements AdminService {

    @Autowired
    BusinessValidation businessValidation;

    @Autowired
    AdminRepo adminRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AdminMapper adminMapper;

    @Autowired
    SubscriberRepo subscriberRepo;

    @Autowired
    PaymentRepo paymentRepo;

    @Autowired
    SupportTicketRepo supportTicketRepo;


    @Override
    public AdminEntity createAdmin(AdminCreateRequestDto adminCreateDto) throws CommonException {
        // Step 1: Check if user already exists by email
        UserEntity existingUser = businessValidation.getUserByEmailAdmin(adminCreateDto.getEmail());

        // Step 2: If user exists
        if (existingUser != null) {
            // Check if already an admin
            businessValidation.getAdminByUserEntity(existingUser);

            existingUser.setRole(EnumUserType.ADMIN);
            userRepo.save(existingUser);
            // Create new Admin
            AdminEntity adminEntity = adminMapper.mapAdminDtoToAdminEntity(adminCreateDto, existingUser);
            return adminRepo.save(adminEntity);
        }

        // Step 3: If user doesn't exist, create new user first
        // Save User
        UserEntity newUser = userMapper.mapUserDtoToUserEntityAdmin(adminCreateDto);
        userRepo.save(newUser);

        // Step 4: Create Admin entry linked to new user
        AdminEntity adminEntity = adminMapper.mapAdminDtoToAdminEntity(adminCreateDto, newUser);
        return adminRepo.save(adminEntity);
    }

    @Override
    public AdminDashboardRequestDto getAdminDashboard(String userId) throws CommonException {

        UserEntity user =businessValidation.getAdminByUserId(userId);

        businessValidation.checkAdminOrNot(user);

        long userCount = userRepo.countByStatusNotAndRoleNot(
                EnumStatusType.DELETE,
                EnumUserType.ADMIN
        );

        long activeSubscriptions = subscriberRepo.countByCurrentSubStatus(EnumSubscriptionStatus.ACTIVE);

        LocalDate today = LocalDate.now();
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);

        List<PaymentEntity> revenueData = paymentRepo.countByPaymentStatusAndPaymentDateBetween(EnumPaymentStatus.SUCCESS, startDate.toString(), today.toString());

        double revenue = revenueData.stream()
                .mapToDouble(PaymentEntity::getAmount)
                .sum();


        List<EnumTicketStatus> pendingStatus = List.of(
                EnumTicketStatus.OPEN,
                EnumTicketStatus.IN_PROGRESS
        );

        long ticketsPending = supportTicketRepo.countByTicketStatusInAndStatusNot(pendingStatus, EnumStatusType.DELETE);

        SubscriptionChartDto statistics = getSubscriptionStatistics();
        List<ActivityDto> recentActivities = getRecentActivities();


        return adminMapper.mapToAdminDashboardRequestDto(userCount, activeSubscriptions, revenue, ticketsPending, statistics, recentActivities);
    }


    private SubscriptionChartDto getSubscriptionStatistics() {

        // Last 6 months start
        LocalDate firstMonth = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        LocalDateTime start = firstMonth.atStartOfDay();
        LocalDateTime today = LocalDateTime.now();

        String startDate = start.toLocalDate().toString();
        String endDate = today.toLocalDate().toString();
        // Exclude cancelled/expired
        List<EnumSubscriptionStatus> excludedStatus = List.of(
                EnumSubscriptionStatus.CANCELLED,
                EnumSubscriptionStatus.EXPIRED
        );

        // Fetch subscriptions
        List<SubscriberEntity> subs = subscriberRepo
                .findAllByCurrentSubStatusNotInAndSubStartDateBetween(
                        excludedStatus,
                        startDate,
                        endDate
                );

        // Prepare chart bucket
        Map<String, Integer> monthMap = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM");

        LocalDate cursor = firstMonth;
        for (int i = 0; i < 6; i++) {
            monthMap.put(cursor.format(fmt), 0);
            cursor = cursor.plusMonths(1);
        }

        // Count subscriptions
        for (SubscriberEntity s : subs) {

            LocalDate subDateParsed = LocalDate.parse(s.getSubStartDate());
            LocalDateTime subDate = subDateParsed.atStartOfDay();

            String month = subDate.format(fmt);

            if (monthMap.containsKey(month)) {
                monthMap.put(month, monthMap.get(month) + 1);
            }
        }

        return new SubscriptionChartDto(
                new ArrayList<>(monthMap.keySet()),
                new ArrayList<>(monthMap.values())
        );
    }


    private List<ActivityDto> getRecentActivities() {

        List<ActivityDto> activities = new ArrayList<>();

        // ⭐ Latest 5 Subscriptions
        List<SubscriberEntity> subs = subscriberRepo.findTop5ByOrderByCreatedAtDesc();

        for (SubscriberEntity s : subs) {
            activities.add(new ActivityDto(
                    s.getUser().getFirstName() + " " + s.getUser().getLastName() +
                            " subscribed to " + s.getPlan().getPlanName(),
                    timeAgo(s.getCreatedAt()),
                    s.getCreatedAt()  // 👈 IMPORTANT
            ));
        }

        // ⭐ Latest 5 Payments
        List<PaymentEntity> payments = paymentRepo.findTop5ByOrderByCreatedAtDesc();

        for (PaymentEntity p : payments) {
            activities.add(new ActivityDto(
                    "Payment of ₹" + p.getAmount() + " received from " + p.getUser().getFirstName() + " " + p.getUser().getLastName(),
                    timeAgo(p.getCreatedAt()),
                    p.getCreatedAt()  // 👈 IMPORTANT
            ));
        }

        // ⭐ Latest 5 Tickets
        List<SupportTicketEntity> tickets = supportTicketRepo.findTop5ByOrderByCreatedAtDesc();

        for (SupportTicketEntity t : tickets) {
            activities.add(new ActivityDto(
                    "Ticket created by " + t.getUser().getFirstName() + " " + t.getUser().getLastName(),
                    timeAgo(t.getCreatedAt()),
                    t.getCreatedAt()  // 👈 IMPORTANT
            ));
        }

        // ⭐ Correct Sorting (by createdAt)
        activities.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        // ⭐ return only top 7
        return activities.stream().limit(7).toList();
    }

    private String timeAgo(LocalDateTime createdAt) {
        if (createdAt == null) return "";
        Duration d = Duration.between(createdAt, LocalDateTime.now());

        if (d.toMinutes() < 1) return "just now";
        if (d.toMinutes() < 60) return d.toMinutes() + " mins ago";
        if (d.toHours() < 24) return d.toHours() + " hours ago";
        return d.toDays() + " days ago";
    }


}
