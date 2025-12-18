package com.banking.controller;

import com.banking.dto.request.LoanPaymentRequest;
import com.banking.dto.response.LoanPaymentResponse;
import com.banking.service.LoanPaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ListIterator;

@RestController
@RequestMapping("/api/loan-payments")
public class LoanPaymentController {

    @Autowired
    private LoanPaymentService loanPaymentService;

    @PostMapping
    public ResponseEntity<LoanPaymentResponse> makePayment(@Valid @RequestBody LoanPaymentRequest request){
        LoanPaymentResponse response= loanPaymentService.makePayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<LoanPaymentResponse>> getPaymentByLoanId(@PathVariable Long loanId){
        List<LoanPaymentResponse> responses= loanPaymentService.getPaymentByLoanId(loanId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<LoanPaymentResponse>> getByAccount(@PathVariable String accountNumber){
        List<LoanPaymentResponse> responses = loanPaymentService.getPaymentByAccountNumber(accountNumber);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/{id}")
    public ResponseEntity<LoanPaymentResponse> getPaymentById(@PathVariable Long id){
        LoanPaymentResponse response= loanPaymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<LoanPaymentResponse> getPaymentByReferenceNum(@PathVariable String referenceNumber){
        LoanPaymentResponse response = loanPaymentService.getPaymentByReferenceNumber(referenceNumber);
        return ResponseEntity.ok(response);
    }
}
