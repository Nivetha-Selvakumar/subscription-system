package com.subscription.subscription_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditResponseDto {
    private String message;
    private int code;
    private UserDetailsDto user;
}