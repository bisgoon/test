package com.hsbc.transaction.management.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "TransactionModify DTO")
public class TransactionModifyDTO {

    @Schema(description = "from account id", example = "1")
    @NotNull(message = "fromAccountId can't be null")
    private Long fromAccountId;

    @Schema(description = "to account id", example = "1")
    @NotNull(message = "toAccountId can't be null")
    private Long toAccountId;

    @Schema(description = "transaction amount", example = "0.01")
    @NotNull(message = "amount can't be null")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2, message = "amount can have two digits")
    @DecimalMin(value = "0.01", inclusive = true, message = "amount must >=0.01")
    private BigDecimal amount;

    @Schema(description = "description", example = "transfer")
    @NotBlank(message = "description can't be blank")
    @Size(min = 3, max = 50, message = "description length should fall within [3,50]")
    private String description;
}
