package com.securebanking.controller;

import com.securebanking.dto.TransactionResponse;
import com.securebanking.service.AuditService;
import com.securebanking.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AuditService auditService;

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(transactionService.getStatistics());
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/customers/{customerId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCustomerId(customerId));
    }

    @PatchMapping("/transactions/{id}/lock")
    public ResponseEntity<TransactionResponse> lockTransaction(
            @PathVariable Long id,
            Authentication authentication,
            HttpServletRequest httpRequest) {

        TransactionResponse response = transactionService.lockTransaction(id);
        auditService.log("LOCK_TRANSACTION", authentication.getName(), id, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/transactions/{id}/cancel")
    public ResponseEntity<TransactionResponse> cancelTransaction(
            @PathVariable Long id,
            Authentication authentication,
            HttpServletRequest httpRequest) {

        TransactionResponse response = transactionService.cancelTransaction(id);
        auditService.log("CANCEL_TRANSACTION", authentication.getName(), id, httpRequest);
        return ResponseEntity.ok(response);
    }
}
