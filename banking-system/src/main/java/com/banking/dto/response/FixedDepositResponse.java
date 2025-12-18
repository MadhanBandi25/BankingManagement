package com.banking.dto.response;

import com.banking.entity.FixedDepositStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedDepositResponse {

    private Long fdId;
    private Long customerId;
    private String customerName;
    private String accountNumber;

    private BigDecimal depositAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;

    private BigDecimal maturityAmount;
    private FixedDepositStatus status;

    private LocalDate startDate;
    private LocalDate maturityDate;

    private BigDecimal prematurePenaltyRate;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;

}
