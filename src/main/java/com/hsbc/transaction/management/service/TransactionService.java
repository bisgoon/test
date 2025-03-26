package com.hsbc.transaction.management.service;

import com.hsbc.transaction.management.model.dto.TransactionDTO;
import com.hsbc.transaction.management.model.dto.TransactionModifyDTO;
import com.hsbc.transaction.management.model.vo.TransactionVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface TransactionService {

    TransactionVO createTransaction(TransactionDTO dto);

    void deleteTransaction(UUID id);

    TransactionVO modifyTransaction(UUID id, TransactionModifyDTO dto);

    Page<TransactionVO> listAllTransactions(String transactionNo, Pageable pageable);

    Optional<TransactionVO> getTransactionById(UUID id);
}
