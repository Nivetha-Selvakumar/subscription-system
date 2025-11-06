package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {
    // User
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String dob;
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
}
