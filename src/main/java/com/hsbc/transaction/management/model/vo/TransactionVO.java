package com.hsbc.transaction.management.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 交易视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Transaction VO")
public class TransactionVO {

    @Schema(description = "transaction id", example = "<uuid>")
    private UUID id;

    @Schema(description = "transaction No", example = "123")
    private String transactionNo;

    @Schema(description = "from account id", example = "1")
    private Long fromAccountId;

    @Schema(description = "to account id", example = "1")
    private Long toAccountId;

    @Schema(description = "transaction amount", example = "0.01")
    private BigDecimal amount;

    @Schema(description = "description", example = "transfer")
    private String description;

    @Schema(description = "deleted at", example = "2025-01-01T01:01:01")
    private Instant deletedAt;

    @Schema(description = "created at", example = "2025-01-01T01:01:01")
    private Instant createdAt;

    @Schema(description = "updated at", example = "2025-01-01T01:01:01")
    private Instant updatedAt;
}
