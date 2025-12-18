package com.banking.service.IMPL;

import com.banking.dto.request.LoanRequest;
import com.banking.dto.response.LoanResponse;
import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.entity.Loan;
import com.banking.entity.LoanStatus;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.repository.LoanRepository;
import com.banking.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public LoanResponse applyLoan(LoanRequest request) {

        Customer customer= customerRepository.findById(request.getCustomerId())
                .orElseThrow(()-> new ResourceNotFoundException("Customer not found: "+ request.getCustomerId()));

        Account account= accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(()-> new ResourceNotFoundException("Account not found: "+ request.getAccountNumber()));

        if(!account.getCustomer().getId().equals(customer.getId())){
            throw new IllegalArgumentException( "Account number" + request.getAccountNumber()+
                    "does NOT belongs to customer id "+ request.getCustomerId());
        }
        Loan loan= new Loan();
        loan.setCustomer(customer);
        loan.setAccount(account);
        loan.setLoanType(request.getLoanType());
        loan.setLoanAmount(request.getLoanAmount());

        BigDecimal interest = getInterestRateByLoanType(request.getLoanType());
        loan.setInterestRate( interest);
        loan.setTenureMonths(request.getTenureMonths());

        BigDecimal emi = calculateEMI(
                request.getLoanAmount(),
                interest,
                request.getTenureMonths()
        );
        loan.setEmiAmount(emi);

        BigDecimal totalPayable = emi
                .multiply(BigDecimal.valueOf(request.getTenureMonths()))
                        .setScale(2,RoundingMode.HALF_UP);

        loan.setRemainingAmount(totalPayable);

        loan.setStatus(LoanStatus.APPLIED);
        loan.setReason(request.getReason());

        Loan saved= loanRepository.save(loan);

        return  mapToLoanResponse(saved);
    }

    //Approved loan
    @Override
    public LoanResponse approveLoan(Long loanId) {
        Loan loan= getLoan(loanId);
        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovedDate(LocalDateTime.now());
        return mapToLoanResponse(loanRepository.save(loan));
    }


    // Reject the loan
    @Override
    public LoanResponse rejectLoan(Long loanId) {

        Loan loan= getLoan(loanId);
        loan.setStatus(LoanStatus.REJECTED);
        return mapToLoanResponse(loanRepository.save(loan));
    }

    //Get lone by customer
    @Override
    public List<LoanResponse> getLoanByCustomer(Long customerId) {
        return loanRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToLoanResponse)
                 .collect(Collectors.toList());
    }

    // Get loan by id
    @Override
    public LoanResponse getLoanById(Long loanId) {
        return mapToLoanResponse(getLoan(loanId));
    }

    // Get loans by account number
    @Override
    public List<LoanResponse> getLoanByAccountNumber(String accountNumber) {
        List<Loan> loans = loanRepository.findByAccount_AccountNumber(accountNumber);
        if(loans.isEmpty()){
            throw new ResourceNotFoundException("No loans found for account Number: "+ accountNumber);
        }
        return loans.stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    // Get all loans
    @Override
    public List<LoanResponse> getAllLoans() {
        return  loanRepository.findAll()
                .stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }


    // helper method
    private  Loan getLoan(Long loanId){
        return loanRepository.findById(loanId)
                .orElseThrow(()-> new ResourceNotFoundException("Loan not found" + loanId));
    }

    // emi calculation
    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal annualRate, Integer months){
        BigDecimal monthlyRate= annualRate
                .divide(BigDecimal.valueOf(1200),10, RoundingMode.HALF_UP);

        BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow(months);
        return principal
                .multiply(monthlyRate)
                .multiply(factor)
                .divide(factor.subtract(BigDecimal.ONE),2,RoundingMode.HALF_UP);
    }

    // business rule
    private BigDecimal getInterestRateByLoanType(Enum<?> loanType){
        return switch (loanType.name()){
            case "HOME" -> new BigDecimal("8.50");
            case "PERSONAL" -> new BigDecimal("12.00");
            case "EDUCATION" -> new BigDecimal("7.50");
            case "VEHICLE" -> new BigDecimal("9.50");
            case "BUSINESS" -> new BigDecimal("11.00");
            default -> throw new IllegalArgumentException("Invalid loan type");
        };
    }

    // response mapping
    private LoanResponse mapToLoanResponse(Loan loan){
        LoanResponse response = new LoanResponse();
        response.setLoanId(loan.getId());
        response.setCustomerId(loan.getCustomer().getId());
        response.setCustomerName(loan.getCustomer().getFirstName()+" "+loan.getCustomer().getLastName());
        response.setAccountNumber(loan.getAccount().getAccountNumber());
        response.setLoanType(loan.getLoanType());
        response.setLoanAmount(loan.getLoanAmount());
        response.setInterestRate(loan.getInterestRate());
        response.setTenureMonths(loan.getTenureMonths());
        response.setEmiAmount(loan.getEmiAmount());
        response.setStatus(loan.getStatus());
        response.setAppliedDate(loan.getCreatedAt());
        response.setApprovedDate(loan.getApprovedDate());
        response.setRemainingAmount(loan.getRemainingAmount());
        response.setReason(loan.getReason());
        return response;
    }
}
