package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.DashboardSummaryDTO;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.exception.UnauthorizedAccessException;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.Role;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    // --- Helper Method for Mock Authentication ---
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    // --- Core CRUD Operations ---

    public FinancialRecord createRecord(FinancialRecord record, Long requesterId) {
        User requester = getUserOrThrow(requesterId);

        // Access Control: Only ADMIN can create records
        if (requester.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only Administrators can create financial records.");
        }

        record.setCreatedBy(requester);
        return recordRepository.save(record);
    }

    public List<FinancialRecord> getAllRecords(Long requesterId) {
        User requester = getUserOrThrow(requesterId);

        // Access Control: VIEWER cannot see individual records
        if (requester.getRole() == Role.VIEWER) {
            throw new UnauthorizedAccessException("Viewers are restricted to dashboard summaries only.");
        }

        return recordRepository.findAll();
    }

    public void deleteRecord(Long recordId, Long requesterId) {
        User requester = getUserOrThrow(requesterId);

        // Access Control: Only ADMIN can delete
        if (requester.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only Administrators can delete financial records.");
        }

        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with ID: " + recordId));

        recordRepository.delete(record);
    }

    // --- Dashboard Analytics ---

    public DashboardSummaryDTO getDashboardSummary(Long requesterId) {
        // We still fetch the user to ensure they are active in the system,
        // but ALL roles (Admin, Analyst, Viewer) can access this method.
        getUserOrThrow(requesterId);

        List<FinancialRecord> allRecords = recordRepository.findAll();

        // Calculate Total Income
        BigDecimal totalIncome = allRecords.stream()
                .filter(r -> r.getType() == TransactionType.INCOME)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate Total Expense
        BigDecimal totalExpense = allRecords.stream()
                .filter(r -> r.getType() == TransactionType.EXPENSE)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate Net Balance
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        // Calculate Category-wise Totals
        Map<String, BigDecimal> categoryTotals = allRecords.stream()
                .collect(Collectors.groupingBy(
                        FinancialRecord::getCategory,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                FinancialRecord::getAmount,
                                BigDecimal::add
                        )
                ));

        return DashboardSummaryDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .categoryWiseTotals(categoryTotals)
                .build();
    }
}