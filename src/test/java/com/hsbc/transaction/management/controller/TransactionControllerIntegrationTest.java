package com.hsbc.transaction.management.controller;

import com.hsbc.transaction.management.dao.TransactionRepo;
import com.hsbc.transaction.management.model.BaseResult;
import com.hsbc.transaction.management.model.dto.TransactionDTO;
import com.hsbc.transaction.management.model.entity.Transaction;
import com.hsbc.transaction.management.model.vo.TransactionVO;
import com.hsbc.transaction.management.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TransactionControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepo transactionRepo;

    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldCrashWhenModifyingExistingTransactionConcurrently() {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .amount(BigDecimal.valueOf(100.0))
                .transactionNo("12345")
                .fromAccountId(1L)
                .toAccountId(2L)
                .description("Test Transaction")
                .build();
        HttpEntity<TransactionDTO> requestEntity = new HttpEntity<>(transactionDTO);
        ResponseEntity<BaseResult<TransactionVO>> response = testRestTemplate.exchange(
                "/transactions", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<BaseResult<TransactionVO>>(){});
        UUID existedTransactionId = response.getBody().getData().getId();

        Transaction existedTransaction = transactionRepo.findById(existedTransactionId).get();
        existedTransaction.setDescription(existedTransaction.getDescription() + " changed");
        transactionRepo.save(existedTransaction);
        assertThrows(
                ObjectOptimisticLockingFailureException.class, //concurrent modification test
                () -> transactionRepo.save(existedTransaction)
        );
    }
}