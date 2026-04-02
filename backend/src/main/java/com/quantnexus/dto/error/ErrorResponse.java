package com.quantnexus.dto.error;

import java.time.LocalDateTime;

/**
 * Standardized Error Blueprint for all API responses.
 */
public record ErrorResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp
) {}
