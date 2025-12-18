package com.banking.controller;

import com.banking.dto.request.FixedDepositRequest;
import com.banking.dto.response.FixedDepositResponse;
import com.banking.service.FixedDepositService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fixed-deposits")
public class FixedDepositController {

    @Autowired
    private FixedDepositService fixedDepositService;

    @PostMapping
    public ResponseEntity<FixedDepositResponse> createFixedDeposit(@Valid @RequestBody FixedDepositRequest request){
        FixedDepositResponse response= fixedDepositService.createFixedDeposit(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("/{fdId}")
    public ResponseEntity<FixedDepositResponse> getFixedDepositById(@PathVariable Long fdId){
        FixedDepositResponse response=fixedDepositService.getFixedDepositById(fdId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<FixedDepositResponse>> getFixedDepositByAccount(@PathVariable String accountNumber){
        List<FixedDepositResponse> responses= fixedDepositService.getFixedDepositsByAccount(accountNumber);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<FixedDepositResponse>>  getAllFixedDeposits(){
        List<FixedDepositResponse> responses= fixedDepositService.getAllFixedDeposits();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{fdId}/closed")
    public ResponseEntity<FixedDepositResponse> closedFixedDeposit(@PathVariable Long fdId){
        FixedDepositResponse response= fixedDepositService.closeFixedDeposit(fdId);
        return ResponseEntity.ok(response);
    }
}
