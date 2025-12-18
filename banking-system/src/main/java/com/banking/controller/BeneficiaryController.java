package com.banking.controller;

import com.banking.dto.request.BeneficiaryRequest;
import com.banking.dto.response.BeneficiaryResponse;
import com.banking.service.BeneficiaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @PostMapping
    public ResponseEntity<BeneficiaryResponse> addBeneficiary(@Valid @RequestBody BeneficiaryRequest request){
        BeneficiaryResponse response= beneficiaryService.addBeneficiary(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<BeneficiaryResponse> approveBeneficiary(@PathVariable Long id){
        BeneficiaryResponse response= beneficiaryService.approveBeneficiary(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<BeneficiaryResponse> rejectBeneficiary(@PathVariable Long id){
        BeneficiaryResponse response= beneficiaryService.rejectBeneficiary(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> getBeneficiaryById(@PathVariable Long id){
        BeneficiaryResponse response= beneficiaryService.getBeneficiaryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<BeneficiaryResponse>> getBeneficiaryByAccount(@PathVariable String accountNumber){
        List<BeneficiaryResponse> responses= beneficiaryService.getBeneficiaryByAccountNumber(accountNumber);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryResponse>> getAllBeneficiaries(){
       List<BeneficiaryResponse> list= beneficiaryService.getAllBeneficiaries();
       return ResponseEntity.ok(list);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBeneficiary(@PathVariable Long id){
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.ok("Beneficiary deleted successfully");
    }
}
