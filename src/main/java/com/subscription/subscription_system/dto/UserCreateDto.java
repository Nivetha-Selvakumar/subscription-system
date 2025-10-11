package com.subscription.subscription_system.dto;

import lombok.Data;

@Data
public class UserCreateDto {
    private String empId;
    private String name;
    private String sex;
    private String email;
    private String phoneNumber;
    private String address;
}
