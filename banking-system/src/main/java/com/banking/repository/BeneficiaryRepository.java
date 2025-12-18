package com.banking.repository;

import com.banking.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary,Long> {

    List<Beneficiary> findByAccount_AccountNumber(String accountNumber);
    boolean existsByAccount_AccountNumberAndBeneficiaryAccountNumber(String accountNumber,
                                                                     String beneficiaryAccountNumber);
}
