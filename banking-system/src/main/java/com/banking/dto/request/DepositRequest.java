package com.banking.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {

    @NotBlank(message = "Account Number is required")
    private String accountNumber;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be greater then 0")
    private BigDecimal amount;

    private String description;
}
