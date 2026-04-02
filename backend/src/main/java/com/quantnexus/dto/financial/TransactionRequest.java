package com.quantnexus.dto.financial;

import com.quantnexus.domain.enums.TransactionCategory;
import com.quantnexus.domain.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Payload for creating a new financial transaction.
 * Enforces strict validation rules before hitting the business logic.
 */
public record TransactionRequest(

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be strictly greater than zero")
        BigDecimal amount,

        @NotNull(message = "Transaction type is required")
        TransactionType type,

        @NotNull(message = "Category is required")
        TransactionCategory category,

        @NotBlank(message = "Description cannot be empty")
        String description,

        @NotNull(message = "Transaction date is required")
        @PastOrPresent(message = "Future transactions are not permitted in the ledger")
        LocalDate transactionDate,

        // Optional metadata (Frontend can pass receipt URLs, merchant IDs, etc.)
        Map<String, String> metadata
) {}