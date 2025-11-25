package com.subscription.subscription_system.mapper;

import com.subscription.subscription_system.dto.ActivityDto;
import com.subscription.subscription_system.dto.AdminCreateRequestDto;
import com.subscription.subscription_system.dto.AdminDashboardRequestDto;
import com.subscription.subscription_system.dto.SubscriptionChartDto;
import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.UserEntity;
import com.subscription.subscription_system.enumuration.EnumStatusType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminMapper {
    public AdminEntity mapAdminDtoToAdminEntity(AdminCreateRequestDto adminCreateDto, UserEntity userEntity) {
        AdminEntity adminEntity = new AdminEntity();
        adminEntity.setUser(userEntity);
        adminEntity.setSalary(
                adminCreateDto.getSalary() != null && !adminCreateDto.getSalary().isEmpty()
                        ? Double.parseDouble(adminCreateDto.getSalary())
                        : 0.0
        );
        adminEntity.setStatus(EnumStatusType.ACTIVE);
        return adminEntity;
    }


    public AdminDashboardRequestDto mapToAdminDashboardRequestDto(long userCount, long activeSubscriptions, double revenue, long ticketsPending, SubscriptionChartDto statistics, List<ActivityDto> recentActivities) {
        AdminDashboardRequestDto adminDashboardRequestDto = new AdminDashboardRequestDto();
        adminDashboardRequestDto.setTotalUsers(userCount);
        adminDashboardRequestDto.setActiveSubscriptions(activeSubscriptions);
        adminDashboardRequestDto.setMonthlyRevenue(revenue);
        adminDashboardRequestDto.setPendingTickets(ticketsPending);
        adminDashboardRequestDto.setRecentActivities(recentActivities);
        adminDashboardRequestDto.setStatistics(statistics);


        return adminDashboardRequestDto;
    }
}
