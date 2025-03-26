package com.hsbc.transaction.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsbc.transaction.management.model.dto.TransactionDTO;
import com.hsbc.transaction.management.model.dto.TransactionModifyDTO;
import com.hsbc.transaction.management.model.vo.TransactionVO;
import com.hsbc.transaction.management.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest{

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTransaction() throws Exception {
        UUID id = UUID.randomUUID();
        TransactionVO transactionVO = TransactionVO.builder()
                .id(id)
                .transactionNo("12345")
                .build();
    
        when(transactionService.createTransaction(any(TransactionDTO.class))).thenReturn(transactionVO);
    
        String transactionDTOJson = "{\"amount\":100.0,\"transactionNo\":\"12345\",\"fromAccountId\":1,\"toAccountId\":2,\"description\":\"Test Transaction\"}";
        
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionDTOJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.transactionNo").value("12345"));
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(transactionService).deleteTransaction(id);
    
        mockMvc.perform(delete("/transactions/{id}", id.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testModifyTransaction() throws Exception {
        UUID id = UUID.randomUUID();
        TransactionModifyDTO dto = TransactionModifyDTO.builder()
                .description("Test Transaction")
                .amount(new BigDecimal("100.00"))
                .fromAccountId(1L)
                .toAccountId(2L)
                .build();

        TransactionVO vo = TransactionVO.builder()
                .id(id)
                .transactionNo("12345")
                .build();
    
        when(transactionService.modifyTransaction(any(UUID.class), any(TransactionModifyDTO.class))).thenReturn(vo);

        mockMvc.perform(put("/transactions/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.transactionNo").value("12345"));
    }

    @Test
    public void testListAllTransactions() throws Exception {
        UUID id = UUID.randomUUID();
        TransactionVO transactionVO = TransactionVO.builder()
                .id(id)
                .transactionNo("12345")
                .build();

        Page<TransactionVO> page = new PageImpl<>(Collections.singletonList(transactionVO));
        when(transactionService.listAllTransactions(anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/transactions")
                .param("transactionNo", "12345")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.data.content[0].transactionNo").value("12345"));
    }

    @Test
    public void testGetTransactionById() throws Exception {
        UUID id = UUID.randomUUID();
        TransactionVO transactionVO = TransactionVO.builder()
                .id(id)
                .transactionNo("12345")
                .build();

        when(transactionService.getTransactionById(any(UUID.class))).thenReturn(Optional.of(transactionVO));

        mockMvc.perform(get("/transactions/" + id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.transactionNo").value("12345"));
    }

    @Test
    public void testGetTransactionByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(transactionService.getTransactionById(any(UUID.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/transactions/" + id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}