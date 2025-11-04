package com.subscription.subscription_system.service;

import com.subscription.subscription_system.dto.AdminCreateRequestDto;
import com.subscription.subscription_system.entity.AdminEntity;
import com.subscription.subscription_system.exception.CommonException;
import org.springframework.stereotype.Component;

@Component
public interface AdminService {


    AdminEntity createAdmin(AdminCreateRequestDto adminCreateDto) throws CommonException;
}
