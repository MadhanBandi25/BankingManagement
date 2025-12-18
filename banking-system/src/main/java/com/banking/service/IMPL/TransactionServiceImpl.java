package com.banking.service.IMPL;

import com.banking.dto.request.DepositRequest;
import com.banking.dto.request.TransferRequest;
import com.banking.dto.request.WithdrawalRequest;
import com.banking.dto.response.TransactionResponse;
import com.banking.entity.Account;
import com.banking.entity.AccountStatus;
import com.banking.entity.Transaction;
import com.banking.entity.TransactionType;
import com.banking.exception.InsufficientBalanceException;
import com.banking.exception.InvalidTransactionException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;

    public static final BigDecimal MINIMUM_BALANCE = new BigDecimal("1000");

    private void validateAccount(Account account){
        if(account.getStatus()!= AccountStatus.ACTIVE){
            throw new InvalidTransactionException("Transactions not allowed for account status: "+ account.getStatus());
        }
    }

    @Override
    public TransactionResponse deposit(DepositRequest request) {

        Account account= accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(()-> new ResourceNotFoundException("Account Not Found: " + request.getAccountNumber()));

        validateAccount(account);
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction= createTransaction( account,TransactionType.DEPOSIT,request.getAmount(),
                account.getBalance(),request.getDescription() !=null ? request.getDescription() : "Cash Deposit",
                account.getAccountNumber(),"CASH DEPOSIT");

        return mapToTnxResponse(transaction);
    }

    @Override
    public TransactionResponse withdraw(WithdrawalRequest request) {
        Account account= accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(()-> new ResourceNotFoundException("Account not found: "+ request.getAccountNumber()));
        validateAccount(account);

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        if(newBalance.compareTo(MINIMUM_BALANCE) < 0){
            throw new InsufficientBalanceException("Minimum balance of ₹1000 must be maintained");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction= createTransaction(account,TransactionType.WITHDRAWAL,request.getAmount(),
                account.getBalance(),request.getDescription() !=null ? request.getDescription(): "Cash Withdrawal",

                "CASH WITHDRAW",account.getAccountNumber());

        return mapToTnxResponse(transaction);
    }

    @Override
    public TransactionResponse transfer(TransferRequest request) {
        if(request.getFromAccountNumber().equals(request.getToAccountNumber())){
            throw new InvalidTransactionException("Cannot transfer to the same account");
        }
        Account fromAccount= accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(()-> new ResourceNotFoundException("From account not found: "+ request.getFromAccountNumber()));
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(()-> new ResourceNotFoundException("To account not found: " + request.getToAccountNumber()));

        validateAccount(fromAccount);
        validateAccount(toAccount);

        BigDecimal newBalance= fromAccount.getBalance().subtract(request.getAmount());
        if(newBalance.compareTo(MINIMUM_BALANCE)< 0){
            throw  new InsufficientBalanceException("Insufficient balance for transfer");
        }
        fromAccount.setBalance(newBalance);
        accountRepository.save(fromAccount);

        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(toAccount);

        Transaction debitTransaction = createTransaction( fromAccount,TransactionType.TRANSFER_DEBIT, request.getAmount(),
                fromAccount.getBalance(),"Transfer to",request.getToAccountNumber(),request.getFromAccountNumber()
                );

         createTransaction(toAccount,TransactionType.TRANSFER_CREDIT,request.getAmount(),
                 toAccount.getBalance(),"Transfer from",
                 request.getToAccountNumber(),request.getFromAccountNumber());

        return mapToTnxResponse(debitTransaction);
    }

    @Override
    public List<TransactionResponse> getTransactionByAccountNumber(String accountNumber) {
        Account account= accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new ResourceNotFoundException("Account Not Found:" +accountNumber));

        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(account.getId())
                .stream()
                .map(this::mapToTnxResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse getTransactionByReferenceNumber(String referenceNumber) {
        Transaction transaction=transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(()-> new ResourceNotFoundException("Transaction not found with reference: "+ referenceNumber));

        TransactionResponse response= modelMapper.map(transaction,TransactionResponse.class);
        response.setAccountNumber(transaction.getAccount().getAccountNumber());
        return response;
    }

    private Transaction createTransaction(Account account, TransactionType type,BigDecimal amount,BigDecimal balanceAfter,
                                       String description,String toAccountNumber, String fromAccountNumber){

        Transaction transaction= new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription(description);
        transaction.setReferenceNumber(generateReferenceNumber());
        transaction.setToAccountNumber(toAccountNumber);
        transaction.setFromAccountNumber(fromAccountNumber);

        return transactionRepository.save(transaction);
    }

    private String generateReferenceNumber(){
        return "TXN"+ UUID.randomUUID().toString().substring(0,12).toUpperCase().replace("-","");
    }

    private TransactionResponse mapToTnxResponse(Transaction transaction){
        TransactionResponse response= modelMapper.map(transaction,TransactionResponse.class);
        response.setAccountNumber(transaction.getAccount().getAccountNumber());


        return response;
    }
}
