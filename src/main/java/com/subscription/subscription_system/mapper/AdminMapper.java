package com.subscription.subscription_system.mapper;

import com.subscription.subscription_system.dto.AdminCreateRequestDto;
import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.entity.UserEntity;
import org.springframework.stereotype.Component;

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
        return adminEntity;
    }
}
