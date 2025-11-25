package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String dateOfBirth;
    private String phoneNumber;
    private String sex;
    private String role;
    private String status;

    // Admin
    private Double salary;

    // Subscriber
    private String currentSubStatus;
    private String subStartDate;
    private String subEndDate;
    private String joinDate;
    private String planName;
    private String planType;

}