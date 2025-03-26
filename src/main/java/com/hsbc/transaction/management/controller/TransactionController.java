package com.hsbc.transaction.management.controller;

import com.hsbc.transaction.management.model.BaseResult;
import com.hsbc.transaction.management.model.dto.TransactionDTO;
import com.hsbc.transaction.management.model.dto.TransactionModifyDTO;
import com.hsbc.transaction.management.model.vo.TransactionVO;
import com.hsbc.transaction.management.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public BaseResult<TransactionVO> createTransaction(@RequestBody @Valid TransactionDTO dto) {
        TransactionVO vo = transactionService.createTransaction(dto);
        return BaseResult.succeed(vo);
    }

    @DeleteMapping("/{id}")
    public BaseResult deleteTransaction(@PathVariable String id) {
        transactionService.deleteTransaction(UUID.fromString(id));
        return BaseResult.succeed();
    }

    @PutMapping("/{id}")
    public BaseResult<TransactionVO> modifyTransaction(@PathVariable String id, @RequestBody @Valid TransactionModifyDTO dto) {
        TransactionVO vo = transactionService.modifyTransaction(UUID.fromString(id), dto);
        return BaseResult.succeed(vo);
    }

    @GetMapping
    public BaseResult<Page<TransactionVO>> listAllTransactions(
            @RequestParam(required = false) String transactionNo,
            @PageableDefault(page = 1, size = 10) Pageable pageable) {
        Pageable adjustedPageable = Pageable.ofSize(pageable.getPageSize()).withPage(pageable.getPageNumber() - 1);
        Page<TransactionVO> transactions = transactionService.listAllTransactions(transactionNo, adjustedPageable);
        return BaseResult.succeed(transactions);
    }

    @GetMapping("/{id}")
    public BaseResult<TransactionVO> getTransactionById(@PathVariable String id) {
        Optional<TransactionVO> transactionOptional = transactionService.getTransactionById(UUID.fromString(id));
        return BaseResult.succeed(transactionOptional.orElse(null));
    }
}
