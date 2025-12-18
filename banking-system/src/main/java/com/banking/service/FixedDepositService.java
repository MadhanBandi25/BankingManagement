package com.banking.service;

import com.banking.dto.request.FixedDepositRequest;
import com.banking.dto.response.FixedDepositResponse;

import java.util.List;

public interface FixedDepositService {

    FixedDepositResponse createFixedDeposit(FixedDepositRequest request);
    FixedDepositResponse getFixedDepositById( Long fdId);
    List<FixedDepositResponse> getFixedDepositsByAccount(String accountNumber);
    List<FixedDepositResponse> getAllFixedDeposits();

    FixedDepositResponse closeFixedDeposit(Long fdId);
}
