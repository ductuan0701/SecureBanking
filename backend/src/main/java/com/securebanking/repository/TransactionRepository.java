package com.securebanking.repository;

import com.securebanking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Transaction> findByIdAndCustomerId(Long id, Long customerId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.deleted = false")
    long countActiveTransactions();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.deleted = false")
    java.math.BigDecimal sumTotalAmount();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.type = 'INTERNAL' AND t.deleted = false")
    long countInternalTransactions();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.type = 'INTERBANK' AND t.deleted = false")
    long countInterbankTransactions();

    @Query("SELECT t FROM Transaction t WHERE t.deleted = false ORDER BY t.createdAt DESC")
    List<Transaction> findAllActiveOrderByCreatedAtDesc();
}
