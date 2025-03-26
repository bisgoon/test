package com.hsbc.transaction.management.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.hsbc.transaction.management.model.dto.TransactionDTO;
import com.hsbc.transaction.management.model.dto.TransactionModifyDTO;
import com.hsbc.transaction.management.model.entity.Transaction;
import com.hsbc.transaction.management.model.vo.TransactionVO;
import com.hsbc.transaction.management.dao.TransactionRepo;
import com.hsbc.transaction.management.exception.BusinessException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class TransactionServiceImplTest{

    @Mock
    private TransactionRepo transactionRepo;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTransactionSuccess() {
        Instant now = Instant.now();
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .transactionNo("12345")
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(BigDecimal.valueOf(100))
                .description("Test Transaction")
                .build();

        Transaction transaction = Transaction.builder()
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(BigDecimal.valueOf(100))
                .description("Test Transaction")
                .transactionNo("12345")
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(transactionRepo.findByTransactionNo("12345")).thenReturn(Optional.empty());
        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);

        TransactionVO result = transactionService.createTransaction(transactionDTO);
        assertNotNull(result);
        assertEquals("12345", result.getTransactionNo());
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        assertEquals("Test Transaction", result.getDescription());
        verify(transactionRepo, times(1)).findByTransactionNo("12345");
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransactionTransactionAlreadyExists() {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .transactionNo("12345")
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(BigDecimal.valueOf(100))
                .description("Test Transaction")
                .build();
    
        when(transactionRepo.findByTransactionNo("12345")).thenReturn(Optional.of(new Transaction()));
    
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.createTransaction(transactionDTO);
        });
        assertEquals("Transaction with transactionNo 12345 already exists.", exception.getMessage());
        verify(transactionRepo, times(1)).findByTransactionNo("12345");
        verify(transactionRepo, times(0)).save(any(Transaction.class));
    }

    @Test
    void testDeleteTransactionTransactionExists() {
        UUID id = UUID.randomUUID();
        when(transactionRepo.existsById(id)).thenReturn(true);
    
        transactionService.deleteTransaction(id);
        verify(transactionRepo, times(1)).deleteById(id);
        verify(transactionRepo, times(1)).existsById(id);
    }

    @Test
    void testDeleteTransactionTransactionDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(transactionRepo.existsById(id)).thenReturn(false);
    
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.deleteTransaction(id);
        });
    
        assertEquals("Transaction with id " + id.toString() + " does not exist.", exception.getMessage());
        verify(transactionRepo, times(1)).existsById(id);
        verify(transactionRepo, times(0)).deleteById(id);
    }

    @Test
    void testModifyTransactionSuccess() {
        UUID id = UUID.randomUUID();
        TransactionModifyDTO transactionModifyDTO = TransactionModifyDTO.builder()
                .fromAccountId(3L)
                .toAccountId(4L)
                .amount(BigDecimal.valueOf(200))
                .description("Modified Transaction")
                .build();

        Transaction transaction = Transaction.builder()
                .id(id)
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(BigDecimal.valueOf(100))
                .description("Original Transaction")
                .transactionNo("12345")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(transactionRepo.findById(id)).thenReturn(Optional.of(transaction));
        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);

        TransactionVO result = transactionService.modifyTransaction(id, transactionModifyDTO);
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(3L, result.getFromAccountId().longValue());
        assertEquals(4L, result.getToAccountId().longValue());
        assertEquals(BigDecimal.valueOf(200), result.getAmount());
        assertEquals("Modified Transaction", result.getDescription());
        verify(transactionRepo, times(1)).findById(id);
        verify(transactionRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void testModifyTransactionTransactionDoesNotExist() {
        UUID id = UUID.randomUUID();
        TransactionModifyDTO transactionModifyDTO = TransactionModifyDTO.builder()
                .fromAccountId(3L)
                .toAccountId(4L)
                .amount(BigDecimal.valueOf(200))
                .description("Modified Transaction")
                .build();

        when(transactionRepo.findById(id)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.modifyTransaction(id, transactionModifyDTO);
        });
        assertEquals("Transaction with id " + id.toString() + " does not exist.", exception.getMessage());
        verify(transactionRepo, times(1)).findById(id);
        verify(transactionRepo, times(0)).save(any(Transaction.class));
    }

    @Test
    void testListAllTransactionsWithTransactionNo() {
        String transactionNo = "12345";
        Pageable pageable = PageRequest.of(0, 10);
        Transaction transaction = Transaction.builder().build();
        Page<Transaction> transactionPage = new PageImpl<>(Collections.singletonList(transaction));
    
        when(transactionRepo.findByTransactionNoContaining(transactionNo, pageable)).thenReturn(transactionPage);
    
        Page<TransactionVO> result = transactionService.listAllTransactions(transactionNo, pageable);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepo, times(1)).findByTransactionNoContaining(transactionNo, pageable);
    }

    @Test
    void testListAllTransactionsWithoutTransactionNo() {
        Pageable pageable = PageRequest.of(0, 10);
        Transaction transaction = Transaction.builder().build();
        Page<Transaction> transactionPage = new PageImpl<>(Collections.singletonList(transaction));
    
        when(transactionRepo.findAll(pageable)).thenReturn(transactionPage);
    
        Page<TransactionVO> result = transactionService.listAllTransactions(null, pageable);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepo, times(1)).findAll(pageable);
    }

    @Test
    void testGetTransactionByIdTransactionExists() {
        UUID id = UUID.randomUUID();
        Transaction transaction = Transaction.builder()
                .id(id).build();
        when(transactionRepo.findById(id)).thenReturn(Optional.of(transaction));
    
        Optional<TransactionVO> result = transactionService.getTransactionById(id);
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(transactionRepo, times(1)).findById(id);
    }

    @Test
    void testGetTransactionByIdTransactionDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(transactionRepo.findById(id)).thenReturn(Optional.empty());
    
        Optional<TransactionVO> result = transactionService.getTransactionById(id);
        assertFalse(result.isPresent());
        verify(transactionRepo, times(1)).findById(id);
    }
}