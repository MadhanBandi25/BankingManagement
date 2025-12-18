package com.banking.service;

import com.banking.dto.request.BeneficiaryRequest;
import com.banking.dto.response.BeneficiaryResponse;
import com.banking.entity.Beneficiary;

import java.util.List;

public interface BeneficiaryService {

    BeneficiaryResponse addBeneficiary(BeneficiaryRequest request);
    BeneficiaryResponse approveBeneficiary(Long beneficiaryId);
    BeneficiaryResponse rejectBeneficiary(Long beneficiaryId);
    BeneficiaryResponse getBeneficiaryById(Long beneficiaryId);

    List<BeneficiaryResponse> getBeneficiaryByAccountNumber(String accountNumber);
    List<BeneficiaryResponse> getAllBeneficiaries();
    void deleteBeneficiary(Long id);
}
