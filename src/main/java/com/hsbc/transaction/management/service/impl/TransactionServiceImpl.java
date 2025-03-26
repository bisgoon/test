package com.hsbc.transaction.management.service.impl;

import com.hsbc.transaction.management.model.dto.TransactionDTO;
import com.hsbc.transaction.management.model.dto.TransactionModifyDTO;
import com.hsbc.transaction.management.model.vo.TransactionVO;
import com.hsbc.transaction.management.dao.TransactionRepo;
import com.hsbc.transaction.management.model.entity.Transaction;
import com.hsbc.transaction.management.exception.BusinessException;
import com.hsbc.transaction.management.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;

    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionVO createTransaction(TransactionDTO dto) {
        Optional<Transaction> existingTransaction = transactionRepo.findByTransactionNo(dto.getTransactionNo());
        if (existingTransaction.isPresent()) {
            throw new BusinessException("Transaction with transactionNo " + dto.getTransactionNo() + " already exists.");
        }
        Transaction transaction = convertToEntity(dto);
        Transaction savedTransaction = transactionRepo.save(transaction);
        return convertToVO(savedTransaction);
    }

    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteTransaction(UUID id) {
        if (!transactionRepo.existsById(id)) {
            throw new BusinessException("Transaction with id " + id + " does not exist.");
        }
        transactionRepo.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionVO modifyTransaction(UUID id, TransactionModifyDTO dto) {
        Optional<Transaction> optionalTransaction = transactionRepo.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new BusinessException("Transaction with id " + id + " does not exist.");
        }
        Transaction transaction = optionalTransaction.get();
        transaction.setFromAccountId(dto.getFromAccountId());
        transaction.setToAccountId(dto.getToAccountId());
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setUpdatedAt(Instant.now());
        Transaction savedTransaction = transactionRepo.save(transaction);
        return convertToVO(savedTransaction);
    }

    @Cacheable(value = "transactions",
            key = "#transactionNo != null && #transactionNo != '' ? #transactionNo + '_' + #pageable.pageNumber + '_' + #pageable.pageSize : 'all_' + #pageable.pageNumber + '_' + #pageable.pageSize",
            unless = "#result == null")
    public Page<TransactionVO> listAllTransactions(String transactionNo, Pageable pageable) {
        Page<Transaction> transactions;
        if (transactionNo != null && !transactionNo.isEmpty()) {
            transactions = transactionRepo.findByTransactionNoContaining(transactionNo, pageable);
        } else {
            transactions = transactionRepo.findAll(pageable);
        }
        return transactions.map(this::convertToVO);
    }

    @Cacheable(value = "transactions", key = "#id")
    public Optional<TransactionVO> getTransactionById(UUID id) {
        Optional<Transaction> optionalTransaction = transactionRepo.findById(id);
        return optionalTransaction.map(this::convertToVO);
    }

    private Transaction convertToEntity(TransactionDTO dto) {
        Instant now = Instant.now();
        Transaction transaction = Transaction.builder()
                .fromAccountId(dto.getFromAccountId())
                .toAccountId(dto.getToAccountId())
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .transactionNo(dto.getTransactionNo())
                .createdAt(now)
                .updatedAt(now)
                .build();
        return transaction;
    }

    private TransactionVO convertToVO(Transaction transaction) {
        TransactionVO vo = TransactionVO.builder()
                .id(transaction.getId())
                .fromAccountId(transaction.getFromAccountId())
                .toAccountId(transaction.getToAccountId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .transactionNo(transaction.getTransactionNo())
                .build();
        return vo;
    }
}
