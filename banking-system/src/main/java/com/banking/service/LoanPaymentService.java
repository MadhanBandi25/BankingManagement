package com.banking.service;

import com.banking.dto.request.LoanPaymentRequest;
import com.banking.dto.response.LoanPaymentResponse;

import java.util.List;

public interface LoanPaymentService {

    LoanPaymentResponse makePayment(LoanPaymentRequest request);
    List<LoanPaymentResponse> getPaymentByLoanId(Long loanId);
    List<LoanPaymentResponse> getPaymentByAccountNumber(String accountNumber);

    LoanPaymentResponse getPaymentById( Long paymentId);
    LoanPaymentResponse getPaymentByReferenceNumber(String referenceNumber);
}
