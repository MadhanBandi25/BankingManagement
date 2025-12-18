package com.banking.dto.response;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanPaymentResponse {

    private Long paymentId;
    private Long loanId;
    private  String  loanType;
    private String accountNumber;
    private String customerName;

    private BigDecimal paymentAmount;
    private LocalDateTime paymentDate;
    private BigDecimal remainingBalance;

    private String paymentReferenceNumber;
    private Integer emiNumber;

    private String loanStatus;
    private String remarks;
}
