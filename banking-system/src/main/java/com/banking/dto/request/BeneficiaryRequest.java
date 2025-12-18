package com.banking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BeneficiaryRequest {

    @NotBlank(message = "Account number is required")
    private String accountNumber;
    @NotBlank(message = "Beneficiary name is required")
    private String beneficiaryName;
    @NotBlank(message = "Beneficiary account number is required")
    private String beneficiaryAccountNumber;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "INVALID IFSC code format")
    private String ifscCode;

    @NotBlank(message = "Bank name is required")
    private String bankName;
}
