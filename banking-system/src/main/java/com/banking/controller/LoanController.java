package com.banking.controller;

import com.banking.dto.request.LoanRequest;
import com.banking.dto.response.LoanResponse;
import com.banking.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> applyLoan(@Valid @RequestBody LoanRequest request){
        LoanResponse response=loanService.applyLoan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{loanId}/approve")
    public ResponseEntity<LoanResponse> approveLoan(@PathVariable Long loanId){
        LoanResponse response=loanService.approveLoan(loanId);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{loanId}/reject")
    public ResponseEntity<LoanResponse> rejectLoan(@PathVariable Long loanId){
        LoanResponse response=loanService.rejectLoan(loanId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanResponse>> getLoanByCustomer(@PathVariable Long customerId){
        List<LoanResponse> responses= loanService.getLoanByCustomer(customerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> getLoneById(@PathVariable Long loanId){
        LoanResponse response=loanService.getLoanById(loanId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<LoanResponse>> getLoansByAccount(@PathVariable String accountNumber){
        List<LoanResponse> response=loanService.getLoanByAccountNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans(){
        List<LoanResponse> loans= loanService.getAllLoans();
        return ResponseEntity.ok(loans);
    }
}
