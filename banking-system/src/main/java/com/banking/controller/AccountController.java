package com.banking.controller;

import com.banking.dto.request.AccountRequest;
import com.banking.dto.response.AccountResponse;
import com.banking.entity.AccountStatus;
import com.banking.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request){
        AccountResponse response= accountService.createAccount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomerId(@PathVariable Long customerId){
        List<AccountResponse> responses=accountService.getAccountByCustomerId(customerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts(){
        List<AccountResponse> responses=accountService.getAllAccounts();
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByAccountNumber(@PathVariable String accountNumber){
        AccountResponse response= accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountNumber){
        BigDecimal balance= accountService.getBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccountResponse>> getByAccountStatus(@PathVariable AccountStatus status){
        List<AccountResponse> responses= accountService.getAccountsByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{accountNumber}/status")
    public ResponseEntity<AccountResponse> changeAccountStatus(@PathVariable String accountNumber,@RequestParam AccountStatus status){
        AccountResponse response=accountService.changeAccountStatus(accountNumber,status);
        return ResponseEntity.ok(response);
    }

}
