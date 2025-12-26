/*package com.bank.banking.controller;

import com.bank.banking.entity.BankAccount;
import com.bank.banking.entity.Transaction;
import com.bank.banking.repository.BankAccountRepository;
import com.bank.banking.repository.TransactionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/official/accounts")
public class TransactionHistoryController {

    private final BankAccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionHistoryController(BankAccountRepository accountRepository,
                                        TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/{accountNumber}")
    public String viewAccountProfile(@PathVariable String accountNumber, Model model) {

        BankAccount account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow();

        List<Transaction> transactions =
                transactionRepository.findByBankAccountOrderByTransactionDateDesc(account);

        model.addAttribute("account", account);
        model.addAttribute("transactions", transactions);

        return "account-profile";
    }
}
*/