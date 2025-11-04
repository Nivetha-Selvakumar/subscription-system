package com.subscription.subscription_system.dto;

import lombok.Data;

@Data
public class UserCreateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String dateOfBirth;
    private String phoneNumber;
    private String sex;
    private String role;
}
