package com.banking.repository;

import com.banking.entity.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanPaymentRepository  extends JpaRepository<LoanPayment, Long> {

    List<LoanPayment> findByLoan_Id(Long loanId);
    List<LoanPayment> findByAccount_Id (Long accountId);

    Optional<LoanPayment> findByPaymentReferenceNumber(String referenceNumber);

    Long countByLoan_Id(Long loanId);
}
