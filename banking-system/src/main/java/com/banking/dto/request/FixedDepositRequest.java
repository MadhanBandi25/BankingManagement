package com.banking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FixedDepositRequest {

    @NotBlank(message = "Account Number is required")
    private String accountNumber;

    @NotNull(message = "Deposit amount is required")
    @DecimalMin(value = "1000" , message = "Minimum FD amount is 1000")
    private BigDecimal depositAmount;

    @NotNull(message = "Tenure is required")
    @Min(value = 3, message = "Minimum tenure is 3 months")
    @Max(value = 120, message = "Maximum tenure is 120 months")
    private Integer tenureMonths;
}
