package com.bank.banking.service;

import com.bank.banking.entity.BankAccount;
import com.bank.banking.repository.BankAccountRepository;
import com.bank.banking.util.AccountNumberGenerator;
import org.springframework.stereotype.Service;

@Service
public class BankAccountService {

    private final BankAccountRepository accountRepository;

    public BankAccountService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public BankAccount createAccount(BankAccount account) {

        String accNo;
        do {
            accNo = AccountNumberGenerator.generate12DigitAccountNumber();
        } while (accountRepository.existsByAccountNumber(accNo));

        account.setAccountNumber(accNo);
        account.setBalance(0);

        return accountRepository.save(account);
    }
}
