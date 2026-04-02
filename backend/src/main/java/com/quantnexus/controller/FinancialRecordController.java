package com.quantnexus.controller;

import com.quantnexus.dto.financial.TransactionRequest;
import com.quantnexus.dto.financial.TransactionResponse;
import com.quantnexus.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * Returns 201 Created on success.
     */
    @PostMapping
    public ResponseEntity<TransactionResponse>createTransaction(
            @RequestParam Long userId,
            @Valid @RequestBody TransactionRequest request){
        log.info("API Request: Create transaction for User {}", userId);
        TransactionResponse response = recordService.createTransaction(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a ledger entry by its unique reference number.
     * Implements dual-path security: validates record ownership or staff bypass.
     */
    @GetMapping("/{refNumber}")
    public ResponseEntity<TransactionResponse>getByReferenceNumber(
            @RequestParam Long userId,
            @PathVariable String refNumber,
            @RequestParam(defaultValue = "false") boolean isStaff){
        log.info("API Request: Fetch record {} for User {}", refNumber, userId);
        return ResponseEntity.ok(recordService.getByReference(userId, refNumber, isStaff));
    }

    /**
     * Fetches a paginated history of transactions.
     * Supports sorting and sizing (e.g., /records?page=0&size=10&sort=transactionDate,desc)
     */
    public ResponseEntity<Page<TransactionResponse>>getTransactionsHistory(
            @RequestParam Long userId,
            @PageableDefault(size = 15) Pageable pageable){
        log.info("API Request: Fetch history for User {}", userId);
        return ResponseEntity.ok(recordService.getHistory(userId, pageable));
    }

    /**
     * Soft-deletes a record from the active ledger.
     * Only accessible by ADMIN (Enforced in Service layer).
     */
    @DeleteMapping("/{refNumber}")
    public ResponseEntity<Void> deleteRecord(@PathVariable String refNumber) {
        log.warn("API Request: Delete record {}", refNumber);
        recordService.deleteRecord(refNumber);
        return ResponseEntity.noContent().build();
    }

}
