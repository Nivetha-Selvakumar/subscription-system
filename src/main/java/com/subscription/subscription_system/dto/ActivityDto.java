package com.subscription.subscription_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto {
    private String message;
    private String time;

    @JsonIgnore       // 👈 do NOT send to frontend
    private LocalDateTime createdAt;
}
