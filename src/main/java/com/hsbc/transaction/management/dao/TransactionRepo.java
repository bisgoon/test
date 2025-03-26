package com.hsbc.transaction.management.dao;

import com.hsbc.transaction.management.model.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByTransactionNoContaining(String transactionNo, Pageable pageable);

    Page<Transaction> findAll(Pageable pageable);

    Optional<Transaction> findByTransactionNo(String transactionNo);
}
