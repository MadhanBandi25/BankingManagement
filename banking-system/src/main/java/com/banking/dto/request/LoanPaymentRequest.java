package com.banking.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanPaymentRequest {

    @NotNull(message = "Loan ID is required")
    private Long loanId;

    @NotBlank(message = "Account Number is required")
    private String accountNumber;

}
