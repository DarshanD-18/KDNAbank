package com.bank.banking.controller;

import com.bank.banking.entity.BankAccount;
import com.bank.banking.repository.BankAccountRepository;
import com.bank.banking.service.AccountNumberGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/official/accounts")
public class BankAccountController {

    private final BankAccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final com.bank.banking.repository.CustomerRepository customerRepository;
    private final com.bank.banking.service.SmsService smsService;

    // ✅ ONLY ONE CONSTRUCTOR (VERY IMPORTANT)
    public BankAccountController(BankAccountRepository accountRepository,
                                 AccountNumberGenerator accountNumberGenerator,
                                 com.bank.banking.repository.CustomerRepository customerRepository,
                                 com.bank.banking.service.SmsService smsService) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.customerRepository = customerRepository;
        this.smsService = smsService;
    }

    // 🔹 VIEW ACCOUNTS
    @GetMapping
    public String viewAccounts(Model model) {
        List<BankAccount> accounts = accountRepository.findAll();
        model.addAttribute("accounts", accounts);
        return "accounts-list";
    }

    // 🔹 SHOW CREATE FORM
    @GetMapping("/create")
public String showCreateForm(Model model) {
    model.addAttribute("account", new BankAccount());
    // supply approved customers for dropdown
    model.addAttribute("approvedCustomers", customerRepository.findByStatus("APPROVED"));
    return "account-create";
}

@PostMapping("/create")
public String createAccount(
        @RequestParam(required = false) Long customerId,
        @RequestParam String accountType,
        @RequestParam double balance,
        Model model) {

    if (customerId == null) {
        model.addAttribute("error", "Please select a customer (approved by manager).");
        model.addAttribute("approvedCustomers", customerRepository.findByStatus("APPROVED"));
        return "account-create";
    }

    var opt = customerRepository.findById(customerId);
    if (opt.isEmpty() || !"APPROVED".equals(opt.get().getStatus())) {
        model.addAttribute("error", "Selected customer is not approved for account creation.");
        model.addAttribute("approvedCustomers", customerRepository.findByStatus("APPROVED"));
        return "account-create";
    }

    var cust = opt.get();

    BankAccount account = new BankAccount();
    account.setCustomerName(cust.getName());
    account.setAccountType(accountType);
    account.setBalance(balance);

    // Generate 12-digit account number
    String accNo = String.valueOf(100000000000L + (long)(Math.random() * 899999999999L));
    account.setAccountNumber(accNo);

    accountRepository.save(account);

    // Send SMS via configured service (Fast2SMS or simulated fallback)
    String sms = String.format("Dear %s, your account has been created successfully. Account No: %s Balance: ₹%.2f", cust.getName(), accNo, balance);
    smsService.sendSms(cust.getMobileNumber(), sms);

    return "redirect:/official/accounts";
}


    @GetMapping("/view/{id}")
public String viewAccount(@PathVariable Long id, Model model) {

    BankAccount account = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found"));

    model.addAttribute("account", account);
    model.addAttribute("transactions", account.getTransactions());

    return "account-profile";
}


    // 🔹 HANDLE CREATE FORM
    
}
