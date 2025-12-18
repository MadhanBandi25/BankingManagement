package com.banking.service.IMPL;

import com.banking.dto.request.BeneficiaryRequest;
import com.banking.dto.response.BeneficiaryResponse;
import com.banking.entity.Account;
import com.banking.entity.Beneficiary;
import com.banking.entity.BeneficiaryStatus;
import com.banking.entity.Customer;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.BeneficiaryRepository;
import com.banking.repository.CustomerRepository;
import com.banking.service.BeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BeneficiaryServiceImpl implements BeneficiaryService {
    @Autowired
    private BeneficiaryRepository beneficiaryRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public BeneficiaryResponse addBeneficiary(BeneficiaryRequest request) {

        Account account= accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(()-> new ResourceNotFoundException("Account Not found: "+ request.getAccountNumber()));

        boolean exits= beneficiaryRepository
                .existsByAccount_AccountNumberAndBeneficiaryAccountNumber(request.getAccountNumber(),
                        request.getBeneficiaryAccountNumber());
        if(exits){
            throw  new IllegalArgumentException(" Beneficiary account " + request.getBeneficiaryAccountNumber() +
                    " already exists for account " + request.getAccountNumber());
        }
        Beneficiary beneficiary = new Beneficiary();

        beneficiary.setAccount(account);
        beneficiary.setBeneficiaryName(request.getBeneficiaryName());
        beneficiary.setBeneficiaryAccountNumber(request.getBeneficiaryAccountNumber());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setIfscCode(request.getIfscCode());
        beneficiary.setStatus(BeneficiaryStatus.PENDING);

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return mapToBfyResponse(saved);

    }

    @Override
    public BeneficiaryResponse approveBeneficiary(Long beneficiaryId) {
        Beneficiary beneficiary= getBeneficiary(beneficiaryId);
        beneficiary.setStatus(BeneficiaryStatus.APPROVED);
        return mapToBfyResponse(beneficiaryRepository.save(beneficiary));
    }

    @Override
    public BeneficiaryResponse rejectBeneficiary(Long beneficiaryId) {
        Beneficiary beneficiary= getBeneficiary(beneficiaryId);
        beneficiary.setStatus(BeneficiaryStatus.REJECTED);
        return mapToBfyResponse(beneficiaryRepository.save(beneficiary));
    }

    @Override
    public BeneficiaryResponse getBeneficiaryById(Long beneficiaryId) {
        return mapToBfyResponse(getBeneficiary(beneficiaryId));
    }


    @Override
    public List<BeneficiaryResponse> getBeneficiaryByAccountNumber(String accountNumber) {

        return beneficiaryRepository.findByAccount_AccountNumber(accountNumber)
                .stream()
                .map(this::mapToBfyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeneficiaryResponse> getAllBeneficiaries() {
        return beneficiaryRepository.findAll()
                .stream()
                .map(this::mapToBfyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBeneficiary(Long id) {
        Beneficiary beneficiary= getBeneficiary(id);
        beneficiaryRepository.delete(beneficiary);

    }

    private Beneficiary getBeneficiary(Long id){
        return beneficiaryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Beneficiary not found with id: "+ id));
    }


    private BeneficiaryResponse mapToBfyResponse(Beneficiary beneficiary){
        BeneficiaryResponse response= new BeneficiaryResponse();
        response.setBeneficiaryId(beneficiary.getId());
        response.setCustomerId(beneficiary.getAccount().getCustomer().getId());
        response.setCustomerName(beneficiary.getAccount().getCustomer().getFirstName() +" "+
                beneficiary.getAccount().getCustomer().getLastName());

        response.setBeneficiaryName(beneficiary.getBeneficiaryName());
        response.setBeneficiaryAccountNumber(beneficiary.getBeneficiaryAccountNumber());
        response.setBankName(beneficiary.getBankName());
        response.setIfscCode(beneficiary.getIfscCode());
        response.setStatus(beneficiary.getStatus());
        response.setCreatedAt(beneficiary.getCreatedAt());
        return response;
    }
}
