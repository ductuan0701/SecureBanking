package com.securebanking.controller;

import com.securebanking.dto.TransactionRequest;
import com.securebanking.dto.TransactionResponse;
import com.securebanking.entity.User;
import com.securebanking.service.AuditService;
import com.securebanking.service.CustomUserDetailsService;
import com.securebanking.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/transactions")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class UserTransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuditService auditService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {

        User currentUser = userDetailsService.findByUsername(authentication.getName());
        TransactionResponse response = transactionService.createTransaction(request, currentUser.getId());

        auditService.log("CREATE_TRANSACTION", currentUser.getUsername(), response.getId(), httpRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(Authentication authentication) {
        User currentUser = userDetailsService.findByUsername(authentication.getName());
        List<TransactionResponse> transactions = transactionService.getUserTransactions(currentUser.getId());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = userDetailsService.findByUsername(authentication.getName());
        TransactionResponse response = transactionService.getUserTransactionById(id, currentUser.getId());
        return ResponseEntity.ok(response);
    }
}
