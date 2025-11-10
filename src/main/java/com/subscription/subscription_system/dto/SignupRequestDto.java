package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignupRequestDto {
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

}
