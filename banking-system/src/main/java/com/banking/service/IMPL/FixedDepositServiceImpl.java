package com.banking.service.IMPL;

import com.banking.dto.request.FixedDepositRequest;
import com.banking.dto.response.FixedDepositResponse;
import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.entity.FixedDeposit;
import com.banking.entity.FixedDepositStatus;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.FixedDepositRepository;
import com.banking.service.FixedDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FixedDepositServiceImpl implements FixedDepositService {

    @Autowired
    private FixedDepositRepository fixedDepositRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public FixedDepositResponse createFixedDeposit(FixedDepositRequest request) {

        Account account=accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(()-> new ResourceNotFoundException("Account not found: "+ request.getAccountNumber()));
        Customer customer= account.getCustomer();

        FixedDeposit fd= new FixedDeposit();
        fd.setAccount(account);
        fd.setCustomer(customer);
        fd.setDepositAmount(request.getDepositAmount());
        fd.setTenureMonths(request.getTenureMonths());

        BigDecimal interestRate= getInterestRate(request.getTenureMonths());
        fd.setInterestRate(interestRate);

        fd.setStartDate(LocalDate.now());
        fd.setMaturityDate(LocalDate.now().plusMonths(request.getTenureMonths()));

        BigDecimal maturityAmount = calculateMaturityAmount(request.getDepositAmount(),interestRate , request.getTenureMonths());

        fd.setMaturityAmount(maturityAmount);
        fd.setStatus(FixedDepositStatus.ACTIVE);
        fd.setPrematurePenaltyRate(new BigDecimal("1.00"));
        FixedDeposit saved = fixedDepositRepository.save(fd);

        return mapToFixedResponse(saved);
    }

    @Override
    public FixedDepositResponse getFixedDepositById(Long fdId) {

        FixedDeposit deposit= fixedDepositRepository.findById(fdId)
                .orElseThrow(()-> new ResourceNotFoundException("FD not found with id: "+ fdId));
        return mapToFixedResponse(deposit);
    }

    @Override
    public List<FixedDepositResponse> getFixedDepositsByAccount(String accountNumber) {
        return fixedDepositRepository.findByAccount_AccountNumber(accountNumber)
                .stream()
                .map(this::mapToFixedResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FixedDepositResponse> getAllFixedDeposits() {
        return fixedDepositRepository.findAll()
                .stream()
                .map(this::mapToFixedResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FixedDepositResponse closeFixedDeposit(Long fdId) {

        FixedDeposit fd= fixedDepositRepository.findById(fdId)
                .orElseThrow(()-> new ResourceNotFoundException("FD not found with id: "+ fdId));

        if(fd.getStatus() == FixedDepositStatus.CLOSED ||
        fd.getStatus() == FixedDepositStatus.PREMATURE_CLOSED){
            throw  new IllegalStateException("FD already closed");
        }
        Account account= fd.getAccount();

        BigDecimal creditAmount;

        if(LocalDate.now().isBefore(fd.getMaturityDate())){
            BigDecimal penalty = fd.getDepositAmount()
                    .multiply(fd.getPrematurePenaltyRate())
                    .divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            creditAmount =fd.getDepositAmount().subtract(penalty);
            fd.setStatus(FixedDepositStatus.PREMATURE_CLOSED);
        }
        else{
            creditAmount = fd.getMaturityAmount();
            fd.setStatus(FixedDepositStatus.MATURED);
        }

        account.setBalance(account.getBalance().add(creditAmount));
        accountRepository.save(account);

        fd.setClosedAt(LocalDateTime.now());

        fixedDepositRepository.save(fd);
        return mapToFixedResponse(fd);
    }

    private BigDecimal calculateMaturityAmount(BigDecimal principal, BigDecimal rate, Integer months){
        BigDecimal interest= principal
                .multiply(rate)
                .multiply(BigDecimal.valueOf(months))
                .divide(BigDecimal.valueOf(1200), 2, RoundingMode.HALF_UP);
        return principal.add(interest);
    }

    private BigDecimal getInterestRate(Integer tenureMonths){
        if(tenureMonths < 6) return new BigDecimal("5.50");
        if (tenureMonths <=12) return new BigDecimal("6.75");
        if(tenureMonths <=36) return new BigDecimal("7.25");
        return new BigDecimal("7.75");
    }


    private FixedDepositResponse mapToFixedResponse(FixedDeposit fd){
        FixedDepositResponse response= new FixedDepositResponse();

        response.setFdId(fd.getId());
        response.setCustomerId(fd.getCustomer().getId());
        response.setCustomerName(fd.getCustomer().getFirstName() + " " + fd.getCustomer().getLastName());
        response.setAccountNumber(fd.getAccount().getAccountNumber());

        response.setDepositAmount(fd.getDepositAmount());
        response.setInterestRate(fd.getInterestRate());
        response.setTenureMonths(fd.getTenureMonths());

        response.setMaturityAmount(fd.getMaturityAmount());
        response.setStatus(fd.getStatus());

        response.setStartDate(fd.getStartDate());
        response.setMaturityDate(fd.getMaturityDate());

        response.setPrematurePenaltyRate(fd.getPrematurePenaltyRate());
        response.setCreatedAt(fd.getCreatedAt());
        response.setClosedAt(fd.getClosedAt());

        return response;
    }
}
