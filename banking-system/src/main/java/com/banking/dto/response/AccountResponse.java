package com.banking.dto.response;

import com.banking.entity.AccountStatus;
import com.banking.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private Long customerId;
    private String customerName;
    private AccountType accountType;
    private AccountStatus status;
    private BigDecimal balance;
    private LocalDate openingDate;
    private String branch;
    private String ifscCode;
    private LocalDateTime createdAt;
}
