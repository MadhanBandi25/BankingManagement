package com.banking.controller;

import com.banking.dto.request.DepositRequest;
import com.banking.dto.request.TransferRequest;
import com.banking.dto.request.WithdrawalRequest;
import com.banking.dto.response.TransactionResponse;
import com.banking.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request){
        TransactionResponse response= transactionService.deposit(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawalRequest request){
        TransactionResponse response=transactionService.withdraw(request);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request){
        TransactionResponse response= transactionService.transfer(request);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountNumber(@PathVariable String accountNumber){
        List<TransactionResponse> list = transactionService.getTransactionByAccountNumber(accountNumber);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<TransactionResponse> getByReference(@PathVariable String referenceNumber){
        TransactionResponse response=transactionService.getTransactionByReferenceNumber(referenceNumber);
        return ResponseEntity.ok(response);
    }
}
