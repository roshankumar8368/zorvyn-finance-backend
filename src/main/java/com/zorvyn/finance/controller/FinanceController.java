package com.zorvyn.finance.controller;

import com.zorvyn.finance.dto.DashboardSummaryDTO;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinancialRecordService financeService;

    // 1. Create a Record (Admin Only)
    @PostMapping("/records")
    public ResponseEntity<FinancialRecord> createRecord(
            @RequestBody FinancialRecord record,
            @RequestHeader("X-User-Id") Long requesterId) {

        FinancialRecord created = financeService.createRecord(record, requesterId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 2. View All Records (Admin & Analyst)
    @GetMapping("/records")
    public ResponseEntity<List<FinancialRecord>> getAllRecords(
            @RequestHeader("X-User-Id") Long requesterId) {

        return ResponseEntity.ok(financeService.getAllRecords(requesterId));
    }

    // 3. Delete a Record (Admin Only)
    @DeleteMapping("/records/{id}")
    public ResponseEntity<Void> deleteRecord(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long requesterId) {

        financeService.deleteRecord(id, requesterId);
        return ResponseEntity.noContent().build();
    }

    // 4. Get Dashboard Summary (All Roles)
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary(
            @RequestHeader("X-User-Id") Long requesterId) {

        return ResponseEntity.ok(financeService.getDashboardSummary(requesterId));
    }
}