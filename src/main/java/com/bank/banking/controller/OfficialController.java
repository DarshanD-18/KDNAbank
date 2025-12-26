package com.bank.banking.controller;

import com.bank.banking.entity.BankAccount;
import com.bank.banking.repository.BankAccountRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/official")
public class OfficialController {

    private final BankAccountRepository accountRepository;

    public OfficialController(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("accountsCount", accountRepository.count());
        model.addAttribute("totalBalance",
                accountRepository.findAll()
                        .stream()
                        .mapToDouble(BankAccount::getBalance)
                        .sum());

        return "official-dashboard";
    }
}
