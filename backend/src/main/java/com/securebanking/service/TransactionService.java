package com.securebanking.service;

import com.securebanking.dto.TransactionRequest;
import com.securebanking.dto.TransactionResponse;
import com.securebanking.entity.*;
import com.securebanking.exception.BadRequestException;
import com.securebanking.repository.TransactionRepository;
import com.securebanking.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final BigDecimal FIFTY_MILLION = new BigDecimal("50000000");

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, Long customerId) {
        TransactionType type;
        try {
            type = TransactionType.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid transaction type: " + request.getType());
        }

        Transaction transaction;

        if (type == TransactionType.INTERNAL) {
            if (request.getReceiverAccount() == null || request.getReceiverAccount().isBlank()) {
                throw new BadRequestException("Receiver account is required for internal transfer");
            }
            InternalTransaction internal = new InternalTransaction();
            internal.setReceiverAccount(request.getReceiverAccount());
            transaction = internal;
        } else {
            if (request.getReceiverBank() == null || request.getReceiverBank().isBlank()) {
                throw new BadRequestException("Receiver bank is required for interbank transfer");
            }
            if (request.getSwiftCode() == null || request.getSwiftCode().isBlank()) {
                throw new BadRequestException("SWIFT code is required for interbank transfer");
            }
            if (request.getAmount().compareTo(FIFTY_MILLION) > 0) {
                if (request.getDigitalSignature() == null || request.getDigitalSignature().isBlank()) {
                    throw new BadRequestException("Digital Signature is required for transactions over 50,000,000 VND");
                }
            }
            InterbankTransaction interbank = new InterbankTransaction();
            interbank.setReceiverBank(request.getReceiverBank());
            interbank.setSwiftCode(request.getSwiftCode());
            interbank.setDigitalSignature(request.getDigitalSignature());
            transaction = interbank;
        }

        transaction.setCustomerId(customerId);
        transaction.setAmount(request.getAmount());
        transaction.setType(type);
        transaction.setSenderAccount(request.getSenderAccount());

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    public List<TransactionResponse> getUserTransactions(Long customerId) {
        return transactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public TransactionResponse getUserTransactionById(Long transactionId, Long customerId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        if (!transaction.getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("Forbidden");
        }
        return mapToResponse(transaction);
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAllActiveOrderByCreatedAtDesc()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponse lockTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        if (transaction.getStatus() == TransactionStatus.LOCKED) {
            throw new BadRequestException("Transaction is already locked");
        }
        if (transaction.getStatus() == TransactionStatus.CANCELLED) {
            throw new BadRequestException("Cannot lock a cancelled transaction");
        }
        transaction.setStatus(TransactionStatus.LOCKED);
        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse cancelTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        if (transaction.getStatus() == TransactionStatus.CANCELLED) {
            throw new BadRequestException("Transaction is already cancelled");
        }
        transaction.setStatus(TransactionStatus.CANCELLED);
        return mapToResponse(transactionRepository.save(transaction));
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTransactions", transactionRepository.countActiveTransactions());
        stats.put("totalAmount", transactionRepository.sumTotalAmount());
        stats.put("internalCount", transactionRepository.countInternalTransactions());
        stats.put("interbankCount", transactionRepository.countInterbankTransactions());
        return stats;
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse res = new TransactionResponse();
        res.setId(transaction.getId());
        res.setTransactionId(transaction.getTransactionId());
        res.setCustomerId(transaction.getCustomerId());
        res.setAmount(transaction.getAmount());
        res.setType(transaction.getType().name());
        res.setStatus(transaction.getStatus().name());
        res.setSenderAccount(transaction.getSenderAccount());
        res.setCreatedAt(transaction.getCreatedAt());

        userRepository.findById(transaction.getCustomerId())
                .ifPresent(user -> res.setCustomerName(user.getFullName()));

        if (transaction instanceof InternalTransaction internal) {
            res.setReceiverAccount(internal.getReceiverAccount());
        } else if (transaction instanceof InterbankTransaction interbank) {
            res.setReceiverBank(interbank.getReceiverBank());
            res.setSwiftCode(interbank.getSwiftCode());
            res.setDigitalSignature(interbank.getDigitalSignature());
        }

        return res;
    }
}
