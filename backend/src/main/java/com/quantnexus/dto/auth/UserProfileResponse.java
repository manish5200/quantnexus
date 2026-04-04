package com.quantnexus.dto.auth;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String email,
        String fullName,
        String role,
        String status,
        String baseCurrency,
        LocalDateTime lastLoginAt,
        Integer failedLoginAttempts,
        LocalDateTime createdAt
) {}