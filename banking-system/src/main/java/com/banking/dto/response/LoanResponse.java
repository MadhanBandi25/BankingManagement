package com.banking.dto.response;

import com.banking.entity.LoanStatus;
import com.banking.entity.LoanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanResponse {

    private Long loanId;
    private Long customerId;
    private String customerName;
    private String accountNumber;
    private LoanType loanType;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal emiAmount;
    private LoanStatus status;
    private LocalDateTime appliedDate;
    private LocalDateTime approvedDate;
    private BigDecimal remainingAmount;
    private String reason;

}
