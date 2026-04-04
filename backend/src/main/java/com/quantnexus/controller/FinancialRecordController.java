package com.quantnexus.controller;

import com.quantnexus.dto.financial.TransactionRequest;
import com.quantnexus.dto.financial.TransactionResponse;
import com.quantnexus.security.SecurityUser;
import com.quantnexus.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for the Financial Ledger.
 * Maps incoming HTTP requests to our Business Intelligence Engine.
 * @author Manish Singh
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    /**
     * Records a new transaction in the ledger.
     * ADMIN ONLY: "Can create, update, and manage records"
     * Returns 201 Created on success.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse>createTransaction(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody TransactionRequest request){
        Long userId = securityUser.getId();
        log.info("API Request: Create transaction for User {}", userId);
        TransactionResponse response = recordService.createTransaction(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a ledger entry by its unique reference number.
     * Implements dual-path security: validates record ownership or staff bypass.
     */
    @GetMapping("/{refNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<TransactionResponse>getByReferenceNumber(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable String refNumber){
        Long userId = securityUser.getId();
        log.info("API Request: Fetch record {} for User {}", refNumber, userId);
        return ResponseEntity.ok(recordService.getByReference(userId, refNumber, true));
    }

    /**
     * Fetches a paginated history of transactions.
     * Supports sorting and sizing (e.g., /records?page=0&size=10&sort=transactionDate,desc)
     */
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<Page<TransactionResponse>>getTransactionsHistory(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PageableDefault(size = 15) Pageable pageable){
        Long userId = securityUser.getId();
        log.info("API Request: Fetch history for User {}", userId);
        return ResponseEntity.ok(recordService.getHistory(userId, pageable));
    }

    /**
     * Soft-deletes a record from the active ledger.
     * Only accessible by ADMIN (Enforced in Service layer).
     */
    @DeleteMapping("/{refNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRecord(@PathVariable String refNumber) {
        log.warn("API Request: Delete record {}", refNumber);
        recordService.deleteRecord(refNumber);
        return ResponseEntity.noContent().build();
    }

}
