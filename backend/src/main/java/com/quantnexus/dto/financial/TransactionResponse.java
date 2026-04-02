package com.quantnexus.dto.financial;

import com.quantnexus.domain.enums.TransactionCategory;
import com.quantnexus.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Secure payload returned to the client.
 * Hides internal database identifiers (UUID) behind the public referenceNumber.
 * Includes the auditable balance snapshot.
 */
public record TransactionResponse(
        String referenceNumber,
        BigDecimal amount,
        BigDecimal balanceAfter, // The Senior Flex: Providing the running balance snapshot
        TransactionType type,
        TransactionCategory category,
        String description,
        LocalDate transactionDate,
        Map<String, String> metadata
) {}