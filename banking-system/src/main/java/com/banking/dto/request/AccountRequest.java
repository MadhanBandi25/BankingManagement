package com.banking.dto.request;

import com.banking.entity.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Account Type is required")
    private AccountType accountType;

    @NotNull
    @DecimalMin(value = "1000.0", message = "Initial deposit must be at least 1000")
    private BigDecimal initialDeposit;

    @NotBlank
    private String branch;

    @NotBlank
    private String ifscCode;

}
