package com.banking.service;

import com.banking.dto.request.AccountRequest;
import com.banking.dto.response.AccountResponse;
import com.banking.entity.AccountStatus;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    AccountResponse createAccount(AccountRequest accountRequest);
    AccountResponse getAccountByAccountNumber(String accountNumber);
    List<AccountResponse> getAccountByCustomerId(Long customerId);
    List<AccountResponse> getAllAccounts();
    BigDecimal getBalance(String accountNumber);

    AccountResponse changeAccountStatus(String accountNumber, AccountStatus status);
    List<AccountResponse> getAccountsByStatus(AccountStatus status);
}
