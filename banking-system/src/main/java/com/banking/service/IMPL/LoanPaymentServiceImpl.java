package com.banking.service.IMPL;

import com.banking.dto.request.LoanPaymentRequest;
import com.banking.dto.response.LoanPaymentResponse;
import com.banking.entity.Account;
import com.banking.entity.Loan;
import com.banking.entity.LoanPayment;
import com.banking.entity.LoanStatus;
import com.banking.exception.InvalidTransactionException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.LoanPaymentRepository;
import com.banking.repository.LoanRepository;
import com.banking.service.LoanPaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanPaymentServiceImpl implements LoanPaymentService {

    @Autowired
    private LoanPaymentRepository loanPaymentRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;

    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("1000");

    @Override
    public LoanPaymentResponse makePayment(LoanPaymentRequest request) {

        Loan loan= loanRepository.findById(request.getLoanId())
                .orElseThrow(()-> new ResourceNotFoundException("Loan not fount with id " + request.getLoanId()));

        if(loan.getStatus() != LoanStatus.APPROVED){
            throw  new InvalidTransactionException("Payment not allowed. Loan status: " +loan.getStatus() + " . Only APPROVED loans can accept payments.");
        }

        Account account= accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(()-> new ResourceNotFoundException("Account not found " + request.getAccountNumber()));

        if(!loan.getAccount().getId().equals(account.getId())){
            throw new InvalidTransactionException("Account does not belongs to this loan");
        }
        BigDecimal emiAmount= loan.getEmiAmount();

        if(loan.getRemainingAmount().compareTo(BigDecimal.ZERO) <=0){
            throw new InvalidTransactionException("Loan already fully paid");
        }

        BigDecimal newRemaining = loan.getRemainingAmount().subtract(emiAmount);

        if(newRemaining.compareTo(BigDecimal.ZERO) < 0 ){
            newRemaining =BigDecimal.ZERO;
        }
        loan.setRemainingAmount(newRemaining);

        int emiNo = loanPaymentRepository.countByLoan_Id(loan.getId()).intValue() + 1;

        String remarks = emiNo +getSuffix(emiNo) + "EMI Payment";
        if(newRemaining.compareTo(BigDecimal.ZERO)== 0){
            loan.setStatus(LoanStatus.CLOSED);
            loan.setApprovedDate(LocalDateTime.now());
        }

        loanRepository.save(loan);

        LoanPayment payment= new LoanPayment();
        payment.setLoan(loan);
        payment.setAccount(account);
        payment.setPaymentAmount(emiAmount);
        payment.setRemainingBalance(newRemaining);
        payment.setEmiNumber(emiNo);
        payment.setPaymentReferenceNumber(generateReference());
        payment.setRemarks(remarks);

        return mapToPayResponse(loanPaymentRepository.save(payment));
    }

    @Override
    public List<LoanPaymentResponse> getPaymentByLoanId(Long loanId) {

        loanRepository.findById(loanId)
                .orElseThrow(()-> new ResourceNotFoundException("Loan not found with id: "+ loanId));

        return loanPaymentRepository.findByLoan_Id(loanId)
                .stream()
                .map(this::mapToPayResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanPaymentResponse> getPaymentByAccountNumber(String accountNumber) {
        Account account= accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new ResourceNotFoundException("Account not found: " + accountNumber));

        return loanPaymentRepository.findByAccount_Id(account.getId())
                .stream().map(this::mapToPayResponse).collect(Collectors.toList());
    }

    @Override
    public LoanPaymentResponse getPaymentById(Long paymentId) {
        LoanPayment payment= loanPaymentRepository.findById(paymentId)
                .orElseThrow(()-> new ResourceNotFoundException("Payment not found: " + paymentId));

        return mapToPayResponse(payment);
    }

    @Override
    public LoanPaymentResponse getPaymentByReferenceNumber(String referenceNumber) {
        LoanPayment payment= loanPaymentRepository.findByPaymentReferenceNumber(referenceNumber)
                .orElseThrow(()-> new ResourceNotFoundException("Payment not found: " + referenceNumber));

        return mapToPayResponse(payment);
    }

    private String generateReference(){
        return  "PAY-" + UUID.randomUUID().toString().substring(0,10).toUpperCase();
    }

    private String getSuffix(int n){
        if(n== 1) return "st";
        if(n==2) return "nd";
        if(n==3) return "rd";
        return "th";
    }

    private LoanPaymentResponse mapToPayResponse(LoanPayment payment){

        LoanPaymentResponse  response= new LoanPaymentResponse();

        response.setPaymentId(payment.getId());
        response.setLoanId(payment.getLoan().getId());

        response.setLoanType(payment.getLoan().getLoanType().name());
        response.setLoanStatus(payment.getLoan().getStatus().name());

        response.setAccountNumber(payment.getAccount().getAccountNumber());
        response.setCustomerName(payment.getAccount().getCustomer().getFirstName() +" " +
                payment.getAccount().getCustomer().getLastName());

        response.setPaymentAmount(payment.getPaymentAmount());
        response.setPaymentDate(payment.getPaymentDate());
        response.setRemainingBalance(payment.getRemainingBalance());
        response.setPaymentReferenceNumber(payment.getPaymentReferenceNumber());
        response.setEmiNumber(payment.getEmiNumber());
        response.setRemarks(payment.getRemarks());
        return response;
    }
}
