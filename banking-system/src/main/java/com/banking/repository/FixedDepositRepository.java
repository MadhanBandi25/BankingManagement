package com.banking.repository;

import com.banking.dto.response.FixedDepositResponse;
import com.banking.entity.FixedDeposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {

    List<FixedDeposit> findByAccount_AccountNumber(String accountNumber);
}
