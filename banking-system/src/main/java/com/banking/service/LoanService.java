package com.banking.service;

import com.banking.dto.request.LoanRequest;
import com.banking.dto.response.LoanResponse;

import java.util.List;

public interface LoanService {

    LoanResponse applyLoan(LoanRequest request);
    LoanResponse approveLoan(Long loanId);
    LoanResponse rejectLoan(Long loanId);
    List<LoanResponse> getLoanByCustomer(Long customerId);
    LoanResponse getLoanById(Long loanId);
    List<LoanResponse> getLoanByAccountNumber(String accountNumber);
    List<LoanResponse> getAllLoans();
 }
