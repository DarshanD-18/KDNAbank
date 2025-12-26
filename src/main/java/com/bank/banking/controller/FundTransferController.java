package com.bank.banking.controller;

import com.bank.banking.entity.BankAccount;
import com.bank.banking.entity.Transaction;
import com.bank.banking.repository.BankAccountRepository;
import com.bank.banking.repository.TransactionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/official/transfer")
public class FundTransferController {

    private final BankAccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public FundTransferController(
            BankAccountRepository accountRepository,
            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // SHOW TRANSFER PAGE
    @GetMapping
    public String transferPage() {
        return "fund-transfer"; // templates/fund-transfer.html
    }

    // PROCESS TRANSFER
    @PostMapping
    public String doTransfer(
            @RequestParam String fromAccount,
            @RequestParam String toAccount,
            @RequestParam double amount,
            Model model) {

        BankAccount from = accountRepository.findByAccountNumber(fromAccount).orElse(null);
        BankAccount to = accountRepository.findByAccountNumber(toAccount).orElse(null);

        if (from == null || to == null) {
            model.addAttribute("error", "Invalid account number");
            return "fund-transfer";
        }

        if (amount <= 0 || from.getBalance() < amount) {
            model.addAttribute("error", "Insufficient balance");
            return "fund-transfer";
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction debit = new Transaction();
        debit.setTransactionType("DEBIT");
        debit.setAmount(amount);
        debit.setTransactionDate(LocalDateTime.now());
        debit.setBankAccount(from);

        Transaction credit = new Transaction();
        credit.setTransactionType("CREDIT");
        credit.setAmount(amount);
        credit.setTransactionDate(LocalDateTime.now());
        credit.setBankAccount(to);

        transactionRepository.save(debit);
        transactionRepository.save(credit);

        return "redirect:/official/accounts";
    }
}
