package com.quantnexus.service;

import com.quantnexus.domain.FinancialRecord;
import com.quantnexus.domain.User;
import com.quantnexus.domain.enums.TransactionType;
import com.quantnexus.dto.financial.TransactionRequest;
import com.quantnexus.dto.financial.TransactionResponse;
import com.quantnexus.dto.financial.TransactionUpdateRequest;
import com.quantnexus.repository.FinancialRecordRepository;
import com.quantnexus.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * This service is the "Brain" of our financial ledger.
 * * @author Manish Singh
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;


    /**
     * Creates a new entry in the ledger.
     * We calculate a "snapshot" of the balance right here so we always have a
     * historical record of the user's balance at that exact moment.
     */
    @Transactional
    public TransactionResponse createTransaction(Long currentUserId, TransactionRequest request){
        log.info("Processing new {} transaction for User ID: {}", request.type(), currentUserId);

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Operational Error: Target user profile not found."));

        //Calculate the new 'balanceAfter' snapshot
        BigDecimal currentBalance = recordRepository
                .findFirstByUserIdOrderByTransactionDateDescCreatedAtDesc(currentUserId)
                .map(FinancialRecord::getBalanceAfter)
                .orElse(BigDecimal.ZERO);

        BigDecimal newBalance = (request.type() == TransactionType.INCOME)
                ? currentBalance.add(request.amount())
                : currentBalance.subtract(request.amount());

        // Build Persistence Model
        FinancialRecord newRecord = FinancialRecord.builder()
                .user(user)
                .amount(request.amount())
                .balanceAfter(newBalance)
                .transactionType(request.type())
                .transactionCategory(request.category())
                .description(request.description())
                .transactionDate(request.transactionDate())
                .metadata(request.metadata())
                .build();

        FinancialRecord savedRecord = recordRepository.save(newRecord);

        log.info("Ledger updated. New balance for {} is now {}",
                user.getFullName(), savedRecord.getBalanceAfter());
        return mapToResponse(savedRecord);
    }

    /**
     * Fetches a single record by reference number with strict security checks.
     * Logic: A user can see their OWN record. Staff (Admin/Analyst) can see ANY record.
     */
    @Transactional(readOnly = true)
    public TransactionResponse getByReference(
            Long currentUserId, String refNumber, boolean isStaff){

        FinancialRecord record = recordRepository.findByReferenceNumber(refNumber)
                .orElseThrow(() -> new EntityNotFoundException("Ledger entry not found: " + refNumber));

        // The "Gatekeeper" logic: Only the owner or staff members get past this point.
        if(!isStaff && !record.getUser().getId().equals(currentUserId)){
            log.error("Security Alert: Unauthorized access attempt to record {} by User {}"
                    , refNumber, currentUserId);
            throw new AccessDeniedException("Access Denied: You do not have permission to view this transaction.");
        }
        return mapToResponse(record);

    }

    /**
     * Grabs a list of all transactions for a user, neatly organized into pages.
     */
    public Page<TransactionResponse> getHistory(Long userId, Pageable pageable){
        log.info("Fetching the full transaction history for user ID: {}", userId);
        return recordRepository.findFirstByUserIdOrderByTransactionDateDescCreatedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }


    /*
     * Removes a record from the active view.
     * Note: We use Soft Delete here. We don't actually erase the data (for audit reasons),
     * we just "hide" it. Only Admins can do this.
     */
    @Transactional
    public void deleteRecord(String referenceNumber) {
        FinancialRecord record = recordRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new EntityNotFoundException("Modification Error: Target record not found."));

        recordRepository.delete(record); // Triggers our @SoftDelete annotation

        log.warn("Record {} has been moved to the 'deleted' archive.", referenceNumber);
    }

    //Helper
    private TransactionResponse mapToResponse(FinancialRecord record) {
        return new TransactionResponse(
                record.getReferenceNumber(),
                record.getAmount(),
                record.getBalanceAfter(),
                record.getTransactionType(),
                record.getTransactionCategory(),
                record.getDescription(),
                record.getTransactionDate(),
                record.getMetadata()
        );
    }

}
