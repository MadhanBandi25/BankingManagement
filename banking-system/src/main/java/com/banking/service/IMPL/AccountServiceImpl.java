package com.banking.service.IMPL;

import com.banking.dto.request.AccountRequest;
import com.banking.dto.response.AccountResponse;
import com.banking.entity.Account;
import com.banking.entity.AccountStatus;
import com.banking.entity.Customer;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.service.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public AccountResponse createAccount(AccountRequest accountRequest) {

        Customer customer= customerRepository.findById(accountRequest.getCustomerId())
                .orElseThrow(()-> new ResourceNotFoundException("Customer not found with id: " + accountRequest.getCustomerId()));

        Account account= new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setCustomer(customer);
        account.setAccountType(accountRequest.getAccountType());
        account.setBalance(accountRequest.getInitialDeposit());
        account.setBranch(accountRequest.getBranch());
        account.setIfscCode(accountRequest.getIfscCode());
        account.setStatus(AccountStatus.ACTIVE);

        Account saved= accountRepository.save(account);
        return mapToResponse(saved);
    }

    // The generation of account number
    private String generateAccountNumber(){
        Random random= new Random();
        long number= 1000000000L + (long) (random.nextDouble() * 9000000000L);
        return String.valueOf(number);
    }

    // mapping

    private AccountResponse mapToResponse(Account account){
        AccountResponse response= modelMapper.map(account, AccountResponse.class);
        response.setCustomerId(account.getCustomer().getId());
        response.setCustomerName(account.getCustomer().getFirstName()+" "+account.getCustomer().getLastName());
        return  response;
    }

    @Override
    public AccountResponse getAccountByAccountNumber(String accountNumber) {
        Account account= accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new ResourceNotFoundException("Account not found: "+ accountNumber));
        return mapToResponse(account);
    }

    @Override
    public List<AccountResponse> getAccountByCustomerId(Long customerId) {
        List<Account> accounts= accountRepository.findByCustomerId(customerId);
        if(accounts.isEmpty()){
            throw new ResourceNotFoundException("No accounts found for customer id: "+ customerId);
        }
        return accounts.stream().
                map(this::mapToResponse).
                collect(Collectors.toList());
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {

        Account account= accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new ResourceNotFoundException("Account not found: "+ accountNumber));
        return account.getBalance();
    }

    @Override
    public AccountResponse changeAccountStatus(String accountNumber, AccountStatus status) {

        Account account= accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new ResourceNotFoundException("Account not found: "+ accountNumber));
        account.setStatus(status);
        Account saved= accountRepository.save(account);
        return mapToResponse(saved);
    }

    @Override
    public List<AccountResponse> getAccountsByStatus(AccountStatus status) {

        List<Account> accounts= accountRepository.findByStatus(status);
        if(accounts.isEmpty()){
            throw new ResourceNotFoundException("No accounts found with status: " + status);
        }
        return accounts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
