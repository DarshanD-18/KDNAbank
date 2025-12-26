package com.bank.banking.controller;

import com.bank.banking.repository.BankAccountRepository;
import com.bank.banking.repository.OfficialRequestRepository;
import com.bank.banking.repository.UserRepository;
import com.bank.banking.repository.CustomerRepository;
import com.bank.banking.entity.User;
import com.bank.banking.entity.BankAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final BankAccountRepository accountRepository;
    private final OfficialRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public ManagerController(BankAccountRepository accountRepository,
                             OfficialRequestRepository requestRepository,
                             UserRepository userRepository,
                             CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("accountsCount", accountRepository.count());
        model.addAttribute("totalBalance",
                accountRepository.findAll()
                        .stream()
                        .mapToDouble(BankAccount::getBalance)
                        .sum());

        model.addAttribute("pendingOfficials",
                requestRepository.countByStatus("PENDING"));

        // include a small list of pending requests for the dashboard recent-requests table
        model.addAttribute("requests", requestRepository.findByStatus("PENDING"));

        // Pending customer registrations
        model.addAttribute("pendingCustomersCount",
            customerCountByStatus("PENDING_APPROVAL"));
        model.addAttribute("pendingCustomers", customerRepository.findByStatusOrderByIdDesc("PENDING_APPROVAL"));

        return "manager-dashboard";
    }

    @GetMapping("/requests")
    public String requests(Model model) {
        model.addAttribute("requests", requestRepository.findByStatus("PENDING"));
        return "manager-requests";
    }

    // Approve a pending customer registration
    @PostMapping("/customers/approve/{id}")
    public String approveCustomer(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        var opt = customerRepository.findById(id);
        if (opt.isPresent()) {
            var c = opt.get();
            c.setStatus("APPROVED");
            customerRepository.save(c);
            redirectAttributes.addFlashAttribute("success", "✅ Customer approved successfully");
        }
        return "redirect:/manager/dashboard";
    }

    // helper to count customers by status
    private long customerCountByStatus(String status) {
        return customerRepository.findByStatus(status).size();
    }

    @PostMapping("/approve/{id}")
    public String approveRequest(@PathVariable Long id) {
        var opt = requestRepository.findById(id);
        if (opt.isPresent()) {
            var req = opt.get();
            // create user if not exists
            if (userRepository.findByUsername(req.getUsername()).isEmpty()) {
                User user = new User();
                user.setUsername(req.getUsername());
                user.setPassword(req.getPassword());
                user.setRole("ROLE_OFFICIAL");
                user.setApproved(true);
                userRepository.save(user);
            }
            req.setStatus("APPROVED");
            requestRepository.save(req);
        }
        return "redirect:/manager/requests";
    }

    @PostMapping("/reject/{id}")
    public String rejectRequest(@PathVariable Long id) {
        var opt = requestRepository.findById(id);
        if (opt.isPresent()) {
            var req = opt.get();
            req.setStatus("REJECTED");
            requestRepository.save(req);
        }
        return "redirect:/manager/requests";
    }

    @GetMapping("/staff")
    public String staff(Model model) {
        model.addAttribute("staffList", userRepository.findByRole("ROLE_OFFICIAL"));
        return "manager-staff";
    }

    @GetMapping("/accounts")
    public String viewAccounts(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "accounts-list";
    }

    @GetMapping("/accounts/view/{id}")
    public String viewAccount(@PathVariable Long id, Model model) {
        var account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        model.addAttribute("account", account);
        model.addAttribute("transactions", account.getTransactions());
        return "account-profile";
    }
}
